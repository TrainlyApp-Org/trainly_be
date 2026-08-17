package com.trainly.backend.controller;

import com.stripe.exception.StripeException;
import com.stripe.exception.SignatureVerificationException;
import com.trainly.backend.dto.BillingStatusResponse;
import com.trainly.backend.security.CurrentUser;
import com.trainly.backend.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/billing")
@RequiredArgsConstructor
public class BillingController {
    private final BillingService billingService;
    private final CurrentUser currentUser;

    @Value("${billing.enabled:false}")
    private boolean billingEnabled;

    @GetMapping("/status")
    public ResponseEntity<BillingStatusResponse> status(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(billingService.getStatus(currentUser.getId(jwt)));
    }

    @PostMapping("/checkout")
    public ResponseEntity<Map<String, String>> checkout(@AuthenticationPrincipal Jwt jwt) throws StripeException {
        requireBillingEnabled();
        return ResponseEntity.ok(Map.of(
                "url",
                billingService.createCheckoutUrl(currentUser.getId(jwt), jwt.getClaimAsString("email"))));
    }

    @PostMapping("/portal")
    public ResponseEntity<Map<String, String>> portal(@AuthenticationPrincipal Jwt jwt) throws StripeException {
        requireBillingEnabled();
        return ResponseEntity.ok(Map.of("url", billingService.createPortalUrl(currentUser.getId(jwt))));
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signature) throws StripeException {
        try {
            billingService.processWebhook(payload, signature);
            return ResponseEntity.ok().build();
        } catch (SignatureVerificationException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    private void requireBillingEnabled() {
        if (!billingEnabled) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Gli abbonamenti saranno disponibili prossimamente"
            );
        }
    }
}
