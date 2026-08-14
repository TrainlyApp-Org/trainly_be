package com.trainly.backend.controller;

import com.trainly.backend.dto.SaveWorkoutSetValueRequest;
import com.trainly.backend.dto.WorkoutSetValueResponse;
import com.trainly.backend.security.CurrentUser;
import com.trainly.backend.service.WorkoutSetValueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workout-set-values")
@RequiredArgsConstructor
public class WorkoutSetValueController {
    private final WorkoutSetValueService service;
    private final CurrentUser currentUser;

    @GetMapping
    public ResponseEntity<Map<String, List<WorkoutSetValueResponse>>> getValues(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam UUID workoutPlanId,
            @RequestParam UUID workoutDayId) {
        return ResponseEntity.ok(Map.of(
                "values", service.getValues(currentUser.getId(jwt), workoutPlanId, workoutDayId)));
    }

    @PutMapping
    public ResponseEntity<Map<String, WorkoutSetValueResponse>> save(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody SaveWorkoutSetValueRequest request) {
        return ResponseEntity.ok(Map.of(
                "value", service.save(currentUser.getId(jwt), request)));
    }
}
