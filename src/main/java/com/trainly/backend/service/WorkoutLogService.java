package com.trainly.backend.service;

import com.trainly.backend.dto.LogWorkoutSetRequest;
import com.trainly.backend.dto.WorkoutLogResponse;
import com.trainly.backend.dto.WorkoutLogSetResponse;
import com.trainly.backend.entity.*;
import com.trainly.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkoutLogService {
    private final WorkoutLogRepository workoutLogRepository;
    private final WorkoutLogDetailRepository detailRepository;
    private final WorkoutPlanRepository workoutPlanRepository;
    private final WorkoutDayRepository workoutDayRepository;
    private final ExerciseRepository exerciseRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    public WorkoutLogResponse start(UUID userId, UUID workoutPlanId, UUID workoutDayId) {
        Optional<WorkoutLog> active = workoutLogRepository
                .findFirstByProfileIdAndWorkoutPlanIdAndWorkoutDayIdAndCompletedAtIsNullOrderByStartedAtDesc(
                        userId, workoutPlanId, workoutDayId.toString());
        if (active.isPresent()) return WorkoutLogResponse.from(active.get());

        WorkoutPlan plan = ownedPlan(userId, workoutPlanId);
        WorkoutDay day = workoutDayRepository.findById(workoutDayId)
                .filter(item -> item.getWorkoutPlan().getId().equals(workoutPlanId))
                .orElseThrow(() -> new IllegalArgumentException("Workout day not found in this plan"));

        WorkoutLog log = WorkoutLog.builder()
                .profile(profileRepository.getReferenceById(userId))
                .workoutPlan(plan)
                .workoutDayId(day.getId().toString())
                .startedAt(OffsetDateTime.now())
                .build();
        return WorkoutLogResponse.from(workoutLogRepository.save(log));
    }

    @Transactional(readOnly = true)
    public Optional<WorkoutLogResponse> active(UUID userId, UUID workoutPlanId, UUID workoutDayId) {
        ownedPlan(userId, workoutPlanId);
        return workoutLogRepository
                .findFirstByProfileIdAndWorkoutPlanIdAndWorkoutDayIdAndCompletedAtIsNullOrderByStartedAtDesc(
                        userId, workoutPlanId, workoutDayId.toString())
                .map(WorkoutLogResponse::from);
    }

    @Transactional
    public WorkoutLogSetResponse saveSet(UUID userId, LogWorkoutSetRequest request) {
        WorkoutLog log = ownedLog(userId, request.getWorkoutLogId());
        if (log.getCompletedAt() != null) {
            throw new IllegalStateException("Workout is already completed");
        }

        Exercise exercise = exerciseRepository.findById(request.getExerciseId())
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found"));
        WorkoutLogDetail detail = detailRepository
                .findByWorkoutLogIdAndExerciseIdAndSetIndex(log.getId(), exercise.getId(), request.getSetIndex())
                .orElseGet(WorkoutLogDetail::new);
        detail.setWorkoutLog(log);
        detail.setExercise(exercise);
        detail.setSetIndex(request.getSetIndex());
        detail.setReps(request.getReps());
        detail.setWeight(request.getWeight());
        detail.setCompleted(request.isCompleted());
        return WorkoutLogSetResponse.from(detailRepository.save(detail));
    }

    @Transactional
    public WorkoutLogResponse complete(UUID userId, UUID workoutLogId) {
        WorkoutLog log = ownedLog(userId, workoutLogId);
        if (log.getCompletedAt() == null) {
            log.setCompletedAt(OffsetDateTime.now());
            workoutLogRepository.save(log);
        }
        return WorkoutLogResponse.from(log);
    }

    private WorkoutPlan ownedPlan(UUID userId, UUID workoutPlanId) {
        WorkoutPlan plan = workoutPlanRepository.findById(workoutPlanId)
                .orElseThrow(() -> new IllegalArgumentException("Workout not found"));
        if (!plan.getProfile().getId().equals(userId)) {
            throw new AccessDeniedException("Workout does not belong to the current user");
        }
        return plan;
    }

    private WorkoutLog ownedLog(UUID userId, UUID workoutLogId) {
        WorkoutLog log = workoutLogRepository.findWithSetsById(workoutLogId)
                .orElseThrow(() -> new IllegalArgumentException("Workout log not found"));
        if (!log.getProfile().getId().equals(userId)) {
            throw new AccessDeniedException("Workout log does not belong to the current user");
        }
        return log;
    }
}
