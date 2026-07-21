package com.trainly.backend.service;

import com.trainly.backend.entity.Profile;
import com.trainly.backend.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {


    private final ProfileRepository profileRepository;


    public Profile getProfile(UUID id) {
        return profileRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Profile not found"));
    }


    public Profile save(Profile profile) {
        return profileRepository.save(profile);
    }


    public void delete(UUID id) {
        profileRepository.deleteById(id);
    }
}
