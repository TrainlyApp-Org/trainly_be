package com.trainly.backend.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkoutPlanRequest {

    private String name;

    private String description;

    private List<WorkoutDayRequest> days;
}
