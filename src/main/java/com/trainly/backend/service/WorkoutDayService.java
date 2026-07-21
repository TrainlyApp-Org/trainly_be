package com.trainly.backend.service;

import com.trainly.backend.entity.WorkoutDay;
import com.trainly.backend.repository.WorkoutDayRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;


import java.util.List;
import java.util.UUID;



@Service
@RequiredArgsConstructor
public class WorkoutDayService {


    private final WorkoutDayRepository repository;



    public List<WorkoutDay> getDays(UUID workoutId){

        return repository
                .findByWorkoutPlanIdOrderByOrderIndexAsc(
                        workoutId
                );
    }



    public WorkoutDay getById(UUID id){

        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Workout day not found"
                        ));
    }



    public WorkoutDay save(WorkoutDay day){

        return repository.save(day);
    }



    public void delete(UUID id){

        repository.deleteById(id);
    }

}
