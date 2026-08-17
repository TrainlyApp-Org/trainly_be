package com.trainly.backend.service;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.trainly.backend.dto.BillingStatusResponse;
import com.trainly.backend.entity.BillingSubscription;
import com.trainly.backend.entity.Profile;
import com.trainly.backend.entity.StripeWebhookEvent;
import com.trainly.backend.repository.BillingSubscriptionRepository;
import com.trainly.backend.repository.ProfileRepository;
import com.trainly.backend.repository.StripeWebhookEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BillingService {
    private final BillingSubscriptionRepository subscriptionRepository;
    private final StripeWebhookEventRepository webhookEventRepository;
    private final ProfileRepository profileRepository;

    @Value("${stripe.secret-key:}")
    private String secretKey;

    @Value("${stripe.webhook-secret:}")
    private String webhookSecret;

    @Value("${stripe.premium-price-id:}")
    private String premiumPriceId;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Transactional
    public String createCheckoutUrl(UUID profileId, String email) throws StripeException {
        configureStripe();
        requireValue(premiumPriceId, "STRIPE_PREMIUM_PRICE_ID");

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));
        BillingSubscription billing = subscriptionRepository.findByProfileId(profileId).orElse(null);
        if (billing == null) {
            billing = createCustomer(profile, email);
        } else {
            updateCustomerEmailIfAvailable(billing.getStripeCustomerId(), email);
        }

        if (isPremiumStatus(billing.getStatus()) && billing.getStripeSubscriptionId() != null) {
            return createPortalUrl(profileId);
        }

        Map<String, Object> lineItem = new HashMap<>();
        lineItem.put("price", premiumPriceId);
        lineItem.put("quantity", 1L);

        Map<String, Object> params = new HashMap<>();
        params.put("mode", "subscription");
        params.put("customer", billing.getStripeCustomerId());
        params.put("client_reference_id", profileId.toString());
        params.put("line_items", List.of(lineItem));
        params.put("success_url", frontendUrl + "/profile?billing=success");
        params.put("cancel_url", frontendUrl + "/profile?billing=cancelled");
        params.put("metadata", Map.of("profile_id", profileId.toString()));

        return Session.create(params).getUrl();
    }

    @Transactional(readOnly = true)
    public BillingStatusResponse getStatus(UUID profileId) {
        return subscriptionRepository.findByProfileId(profileId)
                .map(subscription -> new BillingStatusResponse(
                        isPremiumStatus(subscription.getStatus()),
                        subscription.getStatus(),
                        subscription.isCancelAtPeriodEnd()))
                .orElseGet(() -> new BillingStatusResponse(false, "inactive", false));
    }

    @Transactional(readOnly = true)
    public String createPortalUrl(UUID profileId) throws StripeException {
        configureStripe();
        BillingSubscription billing = subscriptionRepository.findByProfileId(profileId)
                .orElseThrow(() -> new IllegalStateException("No Stripe customer is associated with this profile"));

        Map<String, Object> params = new HashMap<>();
        params.put("customer", billing.getStripeCustomerId());
        params.put("return_url", frontendUrl + "/profile");
        return com.stripe.model.billingportal.Session.create(params).getUrl();
    }

    @Transactional
    public void cancelAtPeriodEnd(UUID profileId) throws StripeException {
        configureStripe();
        BillingSubscription billing = subscriptionRepository.findByProfileId(profileId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Nessun abbonamento Stripe associato all'account"));
        if (billing.getStripeSubscriptionId() == null || !isPremiumStatus(billing.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "L'account non ha un abbonamento Stripe attivo");
        }
        if (billing.isCancelAtPeriodEnd()) return;

        Subscription stripeSubscription = Subscription.retrieve(billing.getStripeSubscriptionId());
        stripeSubscription = stripeSubscription.update(Map.of("cancel_at_period_end", true));
        syncSubscription(billing, stripeSubscription);
    }

    @Transactional
    public void processWebhook(String payload, String signature) throws StripeException {
        configureStripe();
        requireValue(webhookSecret, "STRIPE_WEBHOOK_SECRET");
        requireValue(premiumPriceId, "STRIPE_PREMIUM_PRICE_ID");
        Event event;
        try {
            event = Webhook.constructEvent(payload, signature, webhookSecret);
        } catch (SignatureVerificationException exception) {
            throw exception;
        }

        if (webhookEventRepository.existsById(event.getId())) return;

        switch (event.getType()) {
            case "checkout.session.completed" -> processCheckoutCompleted(event);
            case "customer.subscription.created",
                 "customer.subscription.updated",
                 "customer.subscription.deleted" -> processSubscriptionEvent(event);
            default -> { }
        }

        webhookEventRepository.save(StripeWebhookEvent.builder()
                .eventId(event.getId())
                .eventType(event.getType())
                .build());
    }

    private BillingSubscription createCustomer(Profile profile, String email) throws StripeException {
        Map<String, Object> params = new HashMap<>();
        params.put("name", profile.getFullName() != null ? profile.getFullName() : profile.getUsername());
        params.put("metadata", Map.of("profile_id", profile.getId().toString()));
        if (email != null && !email.isBlank()) {
            params.put("email", email);
        }
        Customer customer = Customer.create(params);

        return subscriptionRepository.save(BillingSubscription.builder()
                .profile(profile)
                .stripeCustomerId(customer.getId())
                .status("inactive")
                .cancelAtPeriodEnd(false)
                .build());
    }

    private void updateCustomerEmailIfAvailable(String customerId, String email) throws StripeException {
        if (email == null || email.isBlank()) return;

        Customer customer = Customer.retrieve(customerId);
        if (!email.equalsIgnoreCase(customer.getEmail())) {
            customer.update(Map.of("email", email));
        }
    }

    private void processCheckoutCompleted(Event event) throws StripeException {
        StripeObject object = event.getDataObjectDeserializer().getObject().orElse(null);
        if (!(object instanceof Session session) || session.getSubscription() == null) return;

        String profileReference = session.getClientReferenceId();
        if ((profileReference == null || profileReference.isBlank()) && session.getMetadata() != null) {
            profileReference = session.getMetadata().get("profile_id");
        }
        if (profileReference == null || profileReference.isBlank()) return;

        UUID profileId = UUID.fromString(profileReference);
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));
        BillingSubscription billing = subscriptionRepository.findByProfileId(profileId)
                .orElseGet(() -> BillingSubscription.builder()
                        .profile(profile)
                        .stripeCustomerId(session.getCustomer())
                        .status("inactive")
                        .build());
        billing.setStripeCustomerId(session.getCustomer());
        syncSubscription(billing, Subscription.retrieve(session.getSubscription()));
    }

    private void processSubscriptionEvent(Event event) {
        StripeObject object = event.getDataObjectDeserializer().getObject().orElse(null);
        if (!(object instanceof Subscription stripeSubscription)) return;

        BillingSubscription billing = subscriptionRepository
                .findByStripeSubscriptionId(stripeSubscription.getId())
                .or(() -> subscriptionRepository.findByStripeCustomerId(stripeSubscription.getCustomer()))
                .orElse(null);
        if (billing != null) syncSubscription(billing, stripeSubscription);
    }

    private void syncSubscription(BillingSubscription billing, Subscription stripeSubscription) {
        billing.setStripeSubscriptionId(stripeSubscription.getId());
        billing.setStatus(stripeSubscription.getStatus());
        billing.setCancelAtPeriodEnd(Boolean.TRUE.equals(stripeSubscription.getCancelAtPeriodEnd()));
        if (stripeSubscription.getItems() != null && !stripeSubscription.getItems().getData().isEmpty()
                && stripeSubscription.getItems().getData().getFirst().getPrice() != null) {
            billing.setStripePriceId(stripeSubscription.getItems().getData().getFirst().getPrice().getId());
        }
        subscriptionRepository.save(billing);

        Profile profile = billing.getProfile();
        profile.setPremium(isPremiumStatus(stripeSubscription.getStatus()) && hasPremiumPrice(stripeSubscription));
        profileRepository.save(profile);
    }

    private boolean hasPremiumPrice(Subscription stripeSubscription) {
        return stripeSubscription.getItems() != null
                && stripeSubscription.getItems().getData().stream()
                .anyMatch(item -> item.getPrice() != null
                        && premiumPriceId.equals(item.getPrice().getId()));
    }

    private boolean isPremiumStatus(String status) {
        return "active".equals(status) || "trialing".equals(status);
    }

    private void configureStripe() {
        requireValue(secretKey, "STRIPE_SECRET_KEY");
        Stripe.apiKey = secretKey;
    }

    private void requireValue(String value, String variableName) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(variableName + " is not configured");
        }
    }
}
