package com.trainly.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;


@Data
public class CreateExerciseRequest {


    @NotBlank(message = "Exercise name is required")
    private String name;


    @NotNull(message = "Category id is required")
    private UUID categoryId;


    private String description;

}
