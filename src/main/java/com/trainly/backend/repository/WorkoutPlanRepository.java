package com.trainly.backend.repository;

import com.trainly.backend.entity.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, UUID>{

    List<WorkoutPlan> findByProfileId(UUID profileId);

    WorkoutPlan findByShareId(UUID shareId);
}
