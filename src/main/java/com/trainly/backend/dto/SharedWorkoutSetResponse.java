package com.trainly.backend.dto;

import com.trainly.backend.entity.SharedWorkoutSet;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record SharedWorkoutSetResponse(
        UUID id, UUID dayId, UUID exerciseId, Integer setIndex,
        BigDecimal weight, Integer reps, OffsetDateTime updatedAt) {
    public static SharedWorkoutSetResponse from(SharedWorkoutSet value) {
        return new SharedWorkoutSetResponse(
                value.getId(), value.getWorkoutDayId(), value.getExerciseId(), value.getSetIndex(),
                value.getWeight(), value.getReps(), value.getUpdatedAt());
    }
}
