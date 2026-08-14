package com.trainly.backend.repository;

import com.trainly.backend.entity.WorkoutSetValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkoutSetValueRepository extends JpaRepository<WorkoutSetValue, UUID> {
    List<WorkoutSetValue> findByProfileIdAndWorkoutPlanIdAndWorkoutDayIdOrderByExerciseIdAscSetIndexAsc(
            UUID profileId, UUID workoutPlanId, UUID workoutDayId);

    Optional<WorkoutSetValue> findByProfileIdAndWorkoutPlanIdAndWorkoutDayIdAndExerciseIdAndSetIndex(
            UUID profileId, UUID workoutPlanId, UUID workoutDayId, UUID exerciseId, Integer setIndex);
}
