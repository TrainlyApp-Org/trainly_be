package com.trainly.backend.dto;

import java.util.List;

public record AdminAccountsPageResponse(
        List<AdminAccountResponse> accounts,
        int page,
        int size,
        long totalElements,
        int totalPages,
        long totalAccounts,
        long premiumAccounts,
        long stripePremiumAccounts,
        long manualPremiumAccounts,
        long workoutPlans
) {}
