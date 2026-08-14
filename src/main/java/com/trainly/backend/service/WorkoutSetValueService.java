package com.trainly.backend.service;

import com.trainly.backend.dto.SaveWorkoutSetValueRequest;
import com.trainly.backend.dto.WorkoutSetValueResponse;
import com.trainly.backend.entity.Exercise;
import com.trainly.backend.entity.WorkoutPlan;
import com.trainly.backend.entity.WorkoutSetValue;
import com.trainly.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkoutSetValueService {
    private final WorkoutSetValueRepository valueRepository;
    private final WorkoutPlanRepository workoutPlanRepository;
    private final WorkoutDayRepository workoutDayRepository;
    private final ExerciseRepository exerciseRepository;
    private final ProfileRepository profileRepository;

    @Transactional(readOnly = true)
    public List<WorkoutSetValueResponse> getValues(UUID profileId, UUID workoutPlanId, UUID workoutDayId) {
        ownedPlanAndDay(profileId, workoutPlanId, workoutDayId);
        return valueRepository
                .findByProfileIdAndWorkoutPlanIdAndWorkoutDayIdOrderByExerciseIdAscSetIndexAsc(
                        profileId, workoutPlanId, workoutDayId)
                .stream()
                .map(WorkoutSetValueResponse::from)
                .toList();
    }

    @Transactional
    public WorkoutSetValueResponse save(UUID profileId, SaveWorkoutSetValueRequest request) {
        WorkoutPlan plan = ownedPlanAndDay(profileId, request.getWorkoutPlanId(), request.getWorkoutDayId());
        Exercise exercise = exerciseRepository.findById(request.getExerciseId())
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found"));

        WorkoutSetValue value = valueRepository
                .findByProfileIdAndWorkoutPlanIdAndWorkoutDayIdAndExerciseIdAndSetIndex(
                        profileId, request.getWorkoutPlanId(), request.getWorkoutDayId(),
                        request.getExerciseId(), request.getSetIndex())
                .orElseGet(WorkoutSetValue::new);

        value.setProfile(profileRepository.getReferenceById(profileId));
        value.setWorkoutPlan(plan);
        value.setWorkoutDayId(request.getWorkoutDayId());
        value.setExercise(exercise);
        value.setSetIndex(request.getSetIndex());
        value.setWeight(request.getWeight());
        value.setReps(request.getReps());
        return WorkoutSetValueResponse.from(valueRepository.save(value));
    }

    private WorkoutPlan ownedPlanAndDay(UUID profileId, UUID workoutPlanId, UUID workoutDayId) {
        WorkoutPlan plan = workoutPlanRepository.findById(workoutPlanId)
                .orElseThrow(() -> new IllegalArgumentException("Workout not found"));
        if (!plan.getProfile().getId().equals(profileId)) {
            throw new AccessDeniedException("Workout does not belong to the current user");
        }
        workoutDayRepository.findById(workoutDayId)
                .filter(item -> item.getWorkoutPlan().getId().equals(workoutPlanId))
                .orElseThrow(() -> new IllegalArgumentException("Workout day not found in this plan"));
        return plan;
    }
}
