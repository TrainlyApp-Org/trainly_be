package com.trainly.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutExerciseResponse {

    private UUID id;

    private UUID exerciseId;

    private String name;

    private String description;

    private Integer sets;

    private String reps;

    private Integer restTime;
}