package com.trainly.backend.service;

import com.trainly.backend.dto.WorkoutCreateRequest;
import com.trainly.backend.dto.WorkoutDayRequest;
import com.trainly.backend.dto.WorkoutExerciseRequest;
import com.trainly.backend.entity.Profile;
import com.trainly.backend.entity.WorkoutDay;
import com.trainly.backend.entity.WorkoutDayExercise;
import com.trainly.backend.entity.WorkoutPlan;
import com.trainly.backend.repository.ExerciseRepository;
import com.trainly.backend.repository.ProfileRepository;
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



        public List<WorkoutPlan> getUserWorkouts(UUID profileId){

                return workoutPlanRepository
                        .findByProfileId(profileId);
        }



        @Transactional
        public WorkoutPlan create(UUID profileId, WorkoutCreateRequest request) {


                WorkoutPlan plan = new WorkoutPlan();

                plan.setProfile(
                        profileRepository.getReferenceById(profileId)
                );

                plan.setName(request.getName());
                plan.setDescription(request.getDescription());
                plan.setShareId(UUID.randomUUID());


                for(WorkoutDayRequest dayRequest : request.getDays()) {

                        WorkoutDay day = new WorkoutDay();

                        day.setName(dayRequest.getName());
                        day.setWorkoutPlan(plan);


                        for(WorkoutExerciseRequest exRequest : dayRequest.getExercises()) {


                        WorkoutDayExercise wde =
                                new WorkoutDayExercise();


                        wde.setWorkoutDay(day);

                        wde.setExercise(
                                exerciseRepository
                                .getReferenceById(exRequest.getExerciseId())
                        );


                        wde.setSets(exRequest.getSets());
                        wde.setReps(exRequest.getReps());
                        wde.setRestTime(exRequest.getRestTime());


                        day.getExercises().add(wde);
                        }


                        plan.getDays().add(day);
                }


                return workoutPlanRepository.save(plan);
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
}