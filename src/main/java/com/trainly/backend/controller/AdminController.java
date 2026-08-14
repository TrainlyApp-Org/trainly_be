package com.trainly.backend.controller;

import com.trainly.backend.dto.*;
import com.trainly.backend.security.AdminAccess;
import com.trainly.backend.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminAccess adminAccess;
    private final AdminService adminService;

    @GetMapping("/status")
    public Map<String, Boolean> status(@AuthenticationPrincipal Jwt jwt) {
        return Map.of("admin", adminAccess.isAdmin(jwt));
    }

    @GetMapping("/accounts")
    public AdminAccountsPageResponse accounts(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "") String query) {
        adminAccess.requireAdmin(jwt);
        return adminService.getAccounts(page, size, query);
    }

    @GetMapping("/accounts/{profileId}")
    public AdminAccountResponse account(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID profileId) {
        adminAccess.requireAdmin(jwt);
        return adminService.getAccount(profileId);
    }

    @GetMapping("/accounts/{profileId}/workouts")
    public Map<String, List<WorkoutPlanDetailsResponse>> workouts(
            @AuthenticationPrincipal Jwt jwt, @PathVariable UUID profileId) {
        adminAccess.requireAdmin(jwt);
        return Map.of("workouts", adminService.getAccountWorkouts(profileId));
    }

    @PatchMapping("/accounts/{profileId}/premium")
    public ResponseEntity<AdminAccountResponse> updatePremium(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID profileId,
            @RequestBody UpdatePremiumRequest request) {
        adminAccess.requireAdmin(jwt);
        return ResponseEntity.ok(adminService.updatePremium(profileId, request.isPremium()));
    }
}
