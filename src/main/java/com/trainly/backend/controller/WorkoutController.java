package com.trainly.backend.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;


import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trainly.backend.dto.ShareWorkoutResponse;
import com.trainly.backend.dto.WorkoutPlanRequest;
import com.trainly.backend.dto.WorkoutPlanResponse;
import com.trainly.backend.entity.WorkoutPlan;
import com.trainly.backend.security.CurrentUser;
import com.trainly.backend.service.WorkoutPlanService;
import com.trainly.backend.dto.WorkoutPlanDetailsResponse;
import com.trainly.backend.dto.SaveSharedWorkoutSetRequest;
import com.trainly.backend.dto.SharedWorkoutSetResponse;
import com.trainly.backend.exception.WorkoutPlanLimitExceededException;

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
        public ResponseEntity<?> createWorkout(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody WorkoutPlanRequest request) {

                UUID profileId = currentUser.getId(jwt);

                try {
                        WorkoutPlan workout = workoutPlanService.create(profileId, request);
                        return ResponseEntity
                                .created(URI.create("/api/v1/workouts/" + workout.getId()))
                                .build();
                } catch (WorkoutPlanLimitExceededException e) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of("error", e.getMessage()));
                }
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

        @PostMapping("/public/{shareId}/weight")
        public ResponseEntity<SharedWorkoutSetResponse> savePublicWorkoutSet(
                @PathVariable UUID shareId,
                @Valid @RequestBody SaveSharedWorkoutSetRequest request) {
                return ResponseEntity.ok(workoutPlanService.savePublicWorkoutSet(shareId, request));
        }

        @GetMapping("/public/{shareId}/values")
        public ResponseEntity<Map<String, List<SharedWorkoutSetResponse>>> getPublicWorkoutSetValues(
                @PathVariable UUID shareId,
                @RequestParam UUID dayId) {
                return ResponseEntity.ok(Map.of(
                        "values", workoutPlanService.getPublicWorkoutSetValues(shareId, dayId)));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteWorkout(@PathVariable UUID id) {
                workoutPlanService.deleteWorkout(id);
                return ResponseEntity.noContent().build();
        }

        @PutMapping("/{id}")
        public ResponseEntity<WorkoutPlanDetailsResponse> updateWorkout(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id, @RequestBody @Valid WorkoutPlanRequest request) {

                UUID profileId = currentUser.getId(jwt);

                WorkoutPlanDetailsResponse response =
                        workoutPlanService.update(id, profileId, request);

                return ResponseEntity.ok(response);
        }
}
