package com.trainly.backend.service;

import com.trainly.backend.dto.ShareWorkoutResponse;
import com.trainly.backend.dto.WorkoutCreateRequest;
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
        public WorkoutPlan create(UUID profileId, WorkoutCreateRequest request) {


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



        @Transactional
        public WorkoutPlan update(
                UUID id,
                WorkoutPlan updated
        ){

                WorkoutPlan existing =
                        getById(id);


                existing.setName(updated.getName());

                existing.setDescription(
                        updated.getDescription()
                );


                return workoutPlanRepository.save(existing);
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


                return mapToDetailsResponse(workoutPlan);
        }

        private WorkoutPlanDetailsResponse mapToDetailsResponse(WorkoutPlan workoutPlan)
        {

                WorkoutPlanDetailsResponse response =
                        new WorkoutPlanDetailsResponse();


                response.setId(workoutPlan.getId());
                response.setName(workoutPlan.getName());
                response.setDescription(workoutPlan.getDescription());
                response.setShareId(workoutPlan.getShareId());
                response.setCreatedAt(workoutPlan.getCreatedAt());


                List<WorkoutDayResponse> days =
                        workoutPlan.getDays()
                        .stream()
                        .map(day -> {

                                WorkoutDayResponse dayResponse =
                                        new WorkoutDayResponse();


                                dayResponse.setId(day.getId());
                                dayResponse.setName(day.getName());
                                dayResponse.setDayOrder(day.getOrderIndex());


                                List<WorkoutExerciseResponse> exercises =
                                        day.getExercises()
                                        .stream()
                                        .map(item -> {

                                        WorkoutExerciseResponse exercise =
                                                new WorkoutExerciseResponse();


                                        exercise.setId(item.getId());

                                        exercise.setExerciseId(
                                                item.getExercise().getId()
                                        );

                                        exercise.setName(
                                                item.getExercise().getName()
                                        );

                                        exercise.setDescription(
                                                item.getExercise().getDescription()
                                        );

                                        exercise.setSets(
                                                item.getSets()
                                        );

                                        exercise.setReps(
                                                item.getReps()
                                        );

                                        exercise.setRestTime(
                                                item.getRestTime()
                                        );


                                        return exercise;

                                        })
                                        .toList();


                                dayResponse.setExercises(exercises);

                                return dayResponse;

                        })
                        .toList();


                response.setDays(days);


                return response;
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

                return mapToDetailsResponse(workoutPlan);
        }
}