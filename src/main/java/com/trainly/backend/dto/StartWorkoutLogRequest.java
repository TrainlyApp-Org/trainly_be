package com.trainly.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class StartWorkoutLogRequest {
    @NotNull
    private UUID workoutPlanId;

    @NotNull
    private UUID workoutDayId;
}
