package com.trainly.backend.service;

import com.trainly.backend.dto.AdminAccountResponse;
import com.trainly.backend.dto.AdminAccountsPageResponse;
import com.trainly.backend.dto.WorkoutPlanDetailsResponse;
import com.trainly.backend.entity.Profile;
import com.trainly.backend.repository.ProfileRepository;
import com.trainly.backend.repository.WorkoutPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final ProfileRepository profileRepository;
    private final WorkoutPlanRepository workoutPlanRepository;

    @Transactional(readOnly = true)
    public AdminAccountsPageResponse getAccounts(int page, int size, String query) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        Page<Profile> profiles = profileRepository.search(
                query == null ? "" : query.trim(),
                PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "updatedAt"))
        );
        List<AdminAccountResponse> accounts = profiles.getContent().stream()
                .map(profile -> toResponse(profile,
                        workoutPlanRepository.countByProfileId(profile.getId())))
                .toList();
        return new AdminAccountsPageResponse(accounts, profiles.getNumber(), profiles.getSize(),
                profiles.getTotalElements(), profiles.getTotalPages(),
                profileRepository.count(), profileRepository.countByPremiumTrue(), workoutPlanRepository.count());
    }

    @Transactional(readOnly = true)
    public AdminAccountResponse getAccount(UUID profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        return toResponse(profile, workoutPlanRepository.countByProfileId(profileId));
    }

    @Transactional(readOnly = true)
    public List<WorkoutPlanDetailsResponse> getAccountWorkouts(UUID profileId) {
        if (!profileRepository.existsById(profileId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
        }
        return workoutPlanRepository.findByProfileId(profileId).stream()
                .map(WorkoutPlanDetailsResponse::mapToDetailsResponse)
                .toList();
    }

    @Transactional
    public AdminAccountResponse updatePremium(UUID profileId, boolean premium) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        profile.setPremium(premium);
        profile.setUpdatedAt(OffsetDateTime.now());
        profileRepository.save(profile);
        return toResponse(profile, workoutPlanRepository.countByProfileId(profileId));
    }

    private AdminAccountResponse toResponse(Profile profile, long workoutCount) {
        return new AdminAccountResponse(profile.getId(), profile.getUsername(),
                profile.getFullName(), profile.isPremium(), profile.getCreatedAt(),
                profile.getUpdatedAt(), workoutCount);
    }
}
