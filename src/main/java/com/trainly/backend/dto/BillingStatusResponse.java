package com.trainly.backend.dto;

public record BillingStatusResponse(
        boolean managed,
        String status,
        boolean cancelAtPeriodEnd
) {
}
