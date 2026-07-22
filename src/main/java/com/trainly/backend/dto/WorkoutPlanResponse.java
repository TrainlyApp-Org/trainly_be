package com.trainly.backend.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WorkoutPlanResponse {

    private UUID id;
    private String name;
    private String description;
    private UUID shareId;
    private Instant createdAt;
}