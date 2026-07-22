package com.trainly.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutPlanDetailsResponse {

    private UUID id;

    private String name;

    private String description;

    private UUID shareId;

    private Instant createdAt;

    private List<WorkoutDayResponse> days;
}
