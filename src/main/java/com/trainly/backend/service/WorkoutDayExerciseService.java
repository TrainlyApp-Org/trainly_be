package com.trainly.backend.service;

import com.trainly.backend.entity.WorkoutDayExercise;
import com.trainly.backend.repository.WorkoutDayExerciseRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;


import java.util.List;
import java.util.UUID;



@Service
@RequiredArgsConstructor
public class WorkoutDayExerciseService {


    private final WorkoutDayExerciseRepository repository;



    public List<WorkoutDayExercise> getExercises(UUID dayId){

        return repository
                .findByWorkoutDayIdOrderByOrderIndexAsc(
                        dayId
                );
    }



    public WorkoutDayExercise save(
            WorkoutDayExercise exercise
    ){

        return repository.save(exercise);
    }



    public void delete(UUID id){

        repository.deleteById(id);
    }

}