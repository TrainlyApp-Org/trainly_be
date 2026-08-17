package com.trainly.backend.repository;

import com.trainly.backend.entity.BillingSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface BillingSubscriptionRepository extends JpaRepository<BillingSubscription, UUID> {
    Optional<BillingSubscription> findByProfileId(UUID profileId);
    Optional<BillingSubscription> findByStripeCustomerId(String customerId);
    Optional<BillingSubscription> findByStripeSubscriptionId(String subscriptionId);
    long countByStatusIn(Collection<String> statuses);
}
