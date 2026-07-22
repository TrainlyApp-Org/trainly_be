package com.trainly.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ShareWorkoutResponse {

    private UUID shareId;
}