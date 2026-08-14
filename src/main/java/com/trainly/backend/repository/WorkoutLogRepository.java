package com.trainly.backend.repository;

import com.trainly.backend.entity.WorkoutLog;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, UUID> {
    @EntityGraph(attributePaths = {"sets", "sets.exercise"})
    Optional<WorkoutLog> findFirstByProfileIdAndWorkoutPlanIdAndWorkoutDayIdAndCompletedAtIsNullOrderByStartedAtDesc(
            UUID profileId, UUID workoutPlanId, String workoutDayId);

    @EntityGraph(attributePaths = {"sets", "sets.exercise"})
    @Query("select log from WorkoutLog log where log.id = :id")
    Optional<WorkoutLog> findWithSetsById(UUID id);
}
