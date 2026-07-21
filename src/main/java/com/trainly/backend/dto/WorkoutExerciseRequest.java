package com.trainly.backend.dto;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkoutExerciseRequest {

    private UUID exerciseId;

    private Integer sets;

    private String reps;

    private Integer restTime;

    private Integer orderIndex;
}
