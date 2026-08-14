package com.trainly.backend.repository;

import com.trainly.backend.entity.WorkoutDayExercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WorkoutDayExerciseRepository extends JpaRepository<WorkoutDayExercise, UUID> {

    List<WorkoutDayExercise> 
    findByWorkoutDayIdOrderByOrderIndexAsc(
            UUID workoutDayId
    );

    void deleteByWorkoutDayId(UUID workoutDayId);

    boolean existsByWorkoutDayIdAndExerciseId(UUID workoutDayId, UUID exerciseId);

}
