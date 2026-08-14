package com.trainly.backend.repository;

import com.trainly.backend.entity.SharedWorkoutSet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface SharedWorkoutSetRepository extends JpaRepository<SharedWorkoutSet, UUID> {
    Optional<SharedWorkoutSet> findByShareIdAndWorkoutDayIdAndExerciseIdAndSetIndex(
            UUID shareId, UUID workoutDayId, UUID exerciseId, Integer setIndex);

    List<SharedWorkoutSet> findByShareIdAndWorkoutDayIdOrderByExerciseIdAscSetIndexAsc(
            UUID shareId, UUID workoutDayId);
}
