package com.trainly.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDto {
    private UUID id;
    private String name;
    private UUID categoryId;
    private String categoryName;
    private String description;
    private boolean isCustom;
    private UUID createdBy;
    private OffsetDateTime createdAt;
}
