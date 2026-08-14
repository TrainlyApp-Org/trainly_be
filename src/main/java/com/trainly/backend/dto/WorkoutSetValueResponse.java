package com.trainly.backend.dto;

import com.trainly.backend.entity.WorkoutSetValue;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class WorkoutSetValueResponse {
    private UUID id;
    private UUID workoutPlanId;
    private UUID workoutDayId;
    private UUID exerciseId;
    private Integer setIndex;
    private BigDecimal weight;
    private Integer reps;

    public static WorkoutSetValueResponse from(WorkoutSetValue value) {
        return WorkoutSetValueResponse.builder()
                .id(value.getId())
                .workoutPlanId(value.getWorkoutPlan().getId())
                .workoutDayId(value.getWorkoutDayId())
                .exerciseId(value.getExercise().getId())
                .setIndex(value.getSetIndex())
                .weight(value.getWeight())
                .reps(value.getReps())
                .build();
    }
}
