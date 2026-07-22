package com.trainly.backend.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trainly.backend.dto.RegisterDto;
import com.trainly.backend.dto.ShareWorkoutResponse;
import com.trainly.backend.dto.WorkoutCreateRequest;
import com.trainly.backend.dto.WorkoutPlanResponse;
import com.trainly.backend.entity.WorkoutPlan;
import com.trainly.backend.security.CurrentUser;
import com.trainly.backend.service.WorkoutPlanService;
import com.trainly.backend.dto.WorkoutPlanDetailsResponse;

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
        public ResponseEntity<Map<String,List<WorkoutPlanResponse>>> getWorkouts(
                @AuthenticationPrincipal Jwt jwt
        ) {

                UUID userId = currentUser.getId(jwt);


                List<WorkoutPlanResponse> workouts =
                        workoutPlanService.getUserWorkouts(userId);


                return ResponseEntity.ok(
                        Map.of(
                                "workouts", workouts
                        )
                );
        }

        @PostMapping()
        public ResponseEntity<Void> createWorkout(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody WorkoutCreateRequest request) {

                UUID profileId = currentUser.getId(jwt);

                WorkoutPlan workout = workoutPlanService.create(profileId, request);


                return ResponseEntity
                .created(URI.create("/api/v1/workouts/" + workout.getId()))
                .build();
        }

        @GetMapping("/{id}")
        public ResponseEntity<WorkoutPlanDetailsResponse> getWorkoutDetails(@PathVariable UUID id)
        {

                WorkoutPlanDetailsResponse response =
                        workoutPlanService.getWorkoutDetails(id);


                return ResponseEntity.ok(response);
        }

        @PostMapping("/{id}/share")
        public ResponseEntity<ShareWorkoutResponse> createShareLink(@PathVariable UUID id)
        {

                ShareWorkoutResponse shareId = workoutPlanService.createShareLink(id);

                return ResponseEntity.ok(shareId);
        }

        @GetMapping("/public/{shareId}")
        public ResponseEntity<WorkoutPlanDetailsResponse> getPublicWorkout(@PathVariable UUID shareId)
        {

                return ResponseEntity.ok(
                        workoutPlanService.getPublicWorkout(shareId)
                );
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteWorkout(@PathVariable UUID id) {
                workoutPlanService.deleteWorkout(id);
                return ResponseEntity.noContent().build();
        }
}
