package com.trainly.backend.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trainly.backend.dto.RegisterDto;
import com.trainly.backend.dto.WorkoutCreateRequest;
import com.trainly.backend.entity.WorkoutPlan;
import com.trainly.backend.security.CurrentUser;
import com.trainly.backend.service.WorkoutPlanService;

import jakarta.validation.Valid;

import org.springframework.security.oauth2.jwt.Jwt;


import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/workouts")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutPlanService workoutPlanService;
    private final CurrentUser currentUser;

    @GetMapping()
    public ResponseEntity<Map<String,List<WorkoutPlan>>> getWorkouts(
            @AuthenticationPrincipal Jwt jwt
    ) {

        UUID userId = currentUser.getId(jwt);


        List<WorkoutPlan> workouts =
                workoutPlanService.getUserWorkouts(userId);


        return ResponseEntity.ok(
                Map.of(
                        "workouts", workouts
                )
        );
    }

    @PostMapping()
    public ResponseEntity<Map<String,WorkoutPlan>> createWorkout(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody WorkoutCreateRequest request) {

        UUID profileId = currentUser.getId(jwt);

        WorkoutPlan workout = workoutPlanService.create(profileId, request);


        return ResponseEntity.ok(
                Map.of(
                        "workout", workout
                )
        );
    }
}
