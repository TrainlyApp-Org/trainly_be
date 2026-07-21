package com.trainly.backend.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkoutDayRequest {

    private String name;

    private Integer orderIndex;

    private List<WorkoutExerciseRequest> exercises;
}
