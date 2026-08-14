package com.trainly.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class SaveSharedWorkoutSetRequest {
    @NotNull private UUID dayId;
    @NotNull private UUID exerciseId;
    @NotNull @Min(0) private Integer setIndex;
    @NotNull @DecimalMin("0.0") private BigDecimal weight;
    @NotNull @Min(0) private Integer reps;
}
