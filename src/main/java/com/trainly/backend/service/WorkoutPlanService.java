package com.trainly.backend.service;

import com.trainly.backend.dto.ShareWorkoutResponse;
import com.trainly.backend.dto.WorkoutPlanRequest;
import com.trainly.backend.dto.WorkoutDayRequest;
import com.trainly.backend.dto.WorkoutDayResponse;
import com.trainly.backend.dto.WorkoutExerciseRequest;
import com.trainly.backend.dto.WorkoutExerciseResponse;
import com.trainly.backend.dto.WorkoutPlanResponse;
import com.trainly.backend.dto.WorkoutPlanDetailsResponse;
import com.trainly.backend.entity.Profile;
import com.trainly.backend.entity.WorkoutDay;
import com.trainly.backend.entity.WorkoutDayExercise;
import com.trainly.backend.entity.WorkoutPlan;
import com.trainly.backend.repository.ExerciseRepository;
import com.trainly.backend.repository.ProfileRepository;
import com.trainly.backend.repository.WorkoutDayExerciseRepository;
import com.trainly.backend.repository.WorkoutDayRepository;
import com.trainly.backend.repository.WorkoutPlanRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;



@Service
@RequiredArgsConstructor
public class WorkoutPlanService {


        private final WorkoutPlanRepository workoutPlanRepository;
        private final ProfileRepository profileRepository;
        private final ExerciseRepository exerciseRepository;
        private final WorkoutDayRepository workoutDayRepository;
        private final WorkoutDayExerciseRepository workoutDayExerciseRepository;



        public List<WorkoutPlanResponse> getUserWorkouts(UUID profileId){

                List<WorkoutPlan> workouts = workoutPlanRepository.findByProfileId(profileId);

                return workouts.stream()
                        .map(w -> new WorkoutPlanResponse(
                                w.getId(),
                                w.getName(),
                                w.getDescription(),
                                w.getShareId(),
                                w.getCreatedAt()
                        ))
                        .toList();
        }



        @Transactional
        public WorkoutPlan create(UUID profileId, WorkoutPlanRequest request) {


                WorkoutPlan plan = new WorkoutPlan();

                plan.setProfile(
                        profileRepository.findById(profileId)
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "Profile not found"
                                        ))
                );

                plan.setName(request.getName());
                plan.setDescription(request.getDescription());

                
                int dayOrder = 0;
                for(WorkoutDayRequest dayRequest : request.getDays()) {
                        int exerciseOrder = 0;
                        WorkoutDay day = new WorkoutDay();

                        day.setName(dayRequest.getName());
                        day.setWorkoutPlan(plan);
                        day.setOrderIndex(dayOrder++);


                        for(WorkoutExerciseRequest exRequest : dayRequest.getExercises()) {


                        WorkoutDayExercise wde =
                                new WorkoutDayExercise();


                        wde.setWorkoutDay(day);

                        wde.setExercise(
                                exerciseRepository.findById(exRequest.getExerciseId())
                                        .orElseThrow(() ->
                                                new RuntimeException(
                                                        "Exercise not found"
                                                ))
                        );


                        wde.setSets(exRequest.getSets());
                        wde.setReps(exRequest.getReps());
                        wde.setRestTime(exRequest.getRestTime());
                        wde.setOrderIndex(exerciseOrder++);

                        day.getExercises().add(wde);
                       
                        }


                        plan.getDays().add(day);
                        
                }

                WorkoutPlan savedPlan = workoutPlanRepository.save(plan);

                return savedPlan;
        }

        public WorkoutPlan getById(UUID id){

                return workoutPlanRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Workout not found"
                                ));
        }



        public void delete(UUID id){

                workoutPlanRepository.deleteById(id);
        }

        @Transactional(readOnly = true)
        public WorkoutPlanDetailsResponse getWorkoutDetails(UUID id) {

                WorkoutPlan workoutPlan =
                        workoutPlanRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Workout not found")
                        );


                return WorkoutPlanDetailsResponse.mapToDetailsResponse(workoutPlan);
        }

        @Transactional
        public ShareWorkoutResponse createShareLink(UUID workoutId) {

                WorkoutPlan workout = workoutPlanRepository.findById(workoutId)
                        .orElseThrow(() -> new RuntimeException("Workout not found"));

                // TODO controllo premium
                /*
                if (!workout.getProfile().isPremium()) {
                        throw new ForbiddenException("Premium required");
                }
                */

                if (workout.getShareId() == null) {
                        workout.setShareId(UUID.randomUUID());
                        workoutPlanRepository.save(workout);
                }

                return new ShareWorkoutResponse(
                        workout.getShareId()
                );
        }

        @Transactional(readOnly = true)
        public WorkoutPlanDetailsResponse getPublicWorkout(UUID shareId) {

                WorkoutPlan workoutPlan = workoutPlanRepository.findByShareId(shareId)
                        .orElseThrow(() ->
                                new RuntimeException("Workout not found")
                        );

                return WorkoutPlanDetailsResponse.mapToDetailsResponse(workoutPlan);
        }

        @Transactional
        public void deleteWorkout(UUID id) {
                if (!workoutPlanRepository.existsById(id)) {
                        throw new EntityNotFoundException("Workout not found");
                }

                workoutPlanRepository.deleteById(id);
        }

        @Transactional
        public WorkoutPlanDetailsResponse update(UUID workoutId, UUID profileId, WorkoutPlanRequest request) {

                WorkoutPlan plan = workoutPlanRepository.findById(workoutId)
                        .orElseThrow(() -> new RuntimeException("Workout not found"));

                if (!plan.getProfile().getId().equals(profileId)) {
                        throw new RuntimeException("Unauthorized");
                }

                plan.setName(request.getName());
                plan.setDescription(request.getDescription());

                // Elimina giorni esistenti
                plan.getDays().clear();

                int dayOrder = 0;

                for (WorkoutDayRequest dayRequest : request.getDays()) {

                        WorkoutDay day = new WorkoutDay();
                        day.setWorkoutPlan(plan);
                        day.setName(dayRequest.getName());
                        day.setOrderIndex(dayOrder++);

                        int exerciseOrder = 0;

                        for (WorkoutExerciseRequest exRequest : dayRequest.getExercises()) {

                        WorkoutDayExercise exercise = new WorkoutDayExercise();

                        exercise.setWorkoutDay(day);

                        exercise.setExercise(
                                exerciseRepository.findById(exRequest.getExerciseId())
                                        .orElseThrow(() -> new RuntimeException("Exercise not found"))
                        );

                        exercise.setSets(exRequest.getSets());
                        exercise.setReps(exRequest.getReps());
                        exercise.setRestTime(exRequest.getRestTime());
                        exercise.setOrderIndex(exerciseOrder++);

                        day.getExercises().add(exercise);
                        }

                        plan.getDays().add(day);
                }

                WorkoutPlan saved = workoutPlanRepository.save(plan);

                return WorkoutPlanDetailsResponse.mapToDetailsResponse(saved);
        }
}