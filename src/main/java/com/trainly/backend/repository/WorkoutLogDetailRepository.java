package com.trainly.backend.repository;

import com.trainly.backend.entity.WorkoutLogDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WorkoutLogDetailRepository extends JpaRepository<WorkoutLogDetail, UUID> {
    Optional<WorkoutLogDetail> findByWorkoutLogIdAndExerciseIdAndSetIndex(
            UUID workoutLogId, UUID exerciseId, Integer setIndex);
}
