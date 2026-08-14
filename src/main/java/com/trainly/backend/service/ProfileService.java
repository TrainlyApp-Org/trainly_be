package com.trainly.backend.service;

import com.trainly.backend.entity.Profile;
import com.trainly.backend.dto.ProfileResponse;
import com.trainly.backend.dto.UpdateProfileRequest;
import com.trainly.backend.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.time.OffsetDateTime;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {


    private final ProfileRepository profileRepository;


    public Profile getProfile(UUID id) {
        return profileRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Profile not found"));
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfileResponse(UUID id) {
        return ProfileResponse.from(getProfile(id));
    }

    @Transactional
    public ProfileResponse updateProfile(UUID id, UpdateProfileRequest request) {
        Profile profile = getProfile(id);
        profile.setUsername(request.getUsername());
        profile.setFullName(request.getFullName());
        profile.setAvatarUrl(request.getAvatarUrl());
        profile.setUpdatedAt(OffsetDateTime.now());
        return ProfileResponse.from(profileRepository.save(profile));
    }


    public Profile save(Profile profile) {
        return profileRepository.save(profile);
    }


    public void delete(UUID id) {
        profileRepository.deleteById(id);
    }
}
