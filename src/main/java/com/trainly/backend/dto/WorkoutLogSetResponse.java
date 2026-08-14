package com.trainly.backend.dto;

import com.trainly.backend.entity.WorkoutLogDetail;

import java.math.BigDecimal;
import java.util.UUID;

public record WorkoutLogSetResponse(
        UUID id,
        UUID exerciseId,
        Integer setIndex,
        Integer reps,
        BigDecimal weight,
        boolean completed) {

    public static WorkoutLogSetResponse from(WorkoutLogDetail detail) {
        return new WorkoutLogSetResponse(
                detail.getId(), detail.getExercise().getId(), detail.getSetIndex(),
                detail.getReps(), detail.getWeight(), detail.isCompleted());
    }
}
