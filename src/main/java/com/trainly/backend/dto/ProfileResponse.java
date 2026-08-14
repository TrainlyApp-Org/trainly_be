package com.trainly.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trainly.backend.entity.Profile;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ProfileResponse {
    private UUID id;
    private String username;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    @JsonProperty("is_premium")
    private boolean premium;

    @JsonProperty("updated_at")
    private OffsetDateTime updatedAt;

    public static ProfileResponse from(Profile profile) {
        return new ProfileResponse(
                profile.getId(),
                profile.getUsername(),
                profile.getFullName(),
                profile.getAvatarUrl(),
                profile.isPremium(),
                profile.getUpdatedAt()
        );
    }
}
