package com.trainly.backend.dto;

import com.trainly.backend.entity.WorkoutLog;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record WorkoutLogResponse(
        UUID id,
        UUID workoutPlanId,
        UUID workoutDayId,
        OffsetDateTime startedAt,
        OffsetDateTime completedAt,
        List<WorkoutLogSetResponse> sets) {

    public static WorkoutLogResponse from(WorkoutLog log) {
        return new WorkoutLogResponse(
                log.getId(),
                log.getWorkoutPlan().getId(),
                UUID.fromString(log.getWorkoutDayId()),
                log.getStartedAt(),
                log.getCompletedAt(),
                log.getSets().stream().map(WorkoutLogSetResponse::from).toList());
    }
}
