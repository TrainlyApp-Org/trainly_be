package com.trainly.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutDayResponse {

    private UUID id;

    private String name;

    private Integer dayOrder;

    private List<WorkoutExerciseResponse> exercises;
}
