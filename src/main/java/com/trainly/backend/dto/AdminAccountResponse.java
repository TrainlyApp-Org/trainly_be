package com.trainly.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AdminAccountResponse(
        UUID id,
        String username,
        @JsonProperty("full_name") String fullName,
        @JsonProperty("is_premium") boolean premium,
        @JsonProperty("created_at") OffsetDateTime createdAt,
        @JsonProperty("updated_at") OffsetDateTime updatedAt,
        @JsonProperty("workout_count") long workoutCount) {}
