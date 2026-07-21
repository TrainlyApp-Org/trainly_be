package com.trainly.backend.repository;

import com.trainly.backend.entity.WorkoutDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WorkoutDayRepository extends JpaRepository<WorkoutDay, UUID> {

    List<WorkoutDay> findByWorkoutPlanIdOrderByOrderIndexAsc(
            UUID workoutPlanId
    );

}
