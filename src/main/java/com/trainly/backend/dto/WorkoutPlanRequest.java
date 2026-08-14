package com.trainly.backend.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Size;

@Getter
@Setter
public class WorkoutPlanRequest {

    private String name;

    @Size(max = 40, message = "La descrizione non può superare 40 caratteri")
    private String description;

    private List<WorkoutDayRequest> days;
}
