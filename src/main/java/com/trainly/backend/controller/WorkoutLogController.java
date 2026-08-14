package com.trainly.backend.controller;

import com.trainly.backend.dto.*;
import com.trainly.backend.security.CurrentUser;
import com.trainly.backend.service.WorkoutLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/logs")
@RequiredArgsConstructor
public class WorkoutLogController {
    private final WorkoutLogService workoutLogService;
    private final CurrentUser currentUser;

    @PostMapping("/start")
    public ResponseEntity<Map<String, WorkoutLogResponse>> start(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody StartWorkoutLogRequest request) {
        WorkoutLogResponse log = workoutLogService.start(
                currentUser.getId(jwt), request.getWorkoutPlanId(), request.getWorkoutDayId());
        return ResponseEntity.ok(Map.of("workoutLog", log));
    }

    @GetMapping("/active")
    public ResponseEntity<?> active(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam UUID workoutPlanId,
            @RequestParam UUID workoutDayId) {
        return workoutLogService.active(currentUser.getId(jwt), workoutPlanId, workoutDayId)
                .<ResponseEntity<?>>map(log -> ResponseEntity.ok(Map.of("workoutLog", log)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/set")
    public ResponseEntity<Map<String, WorkoutLogSetResponse>> saveSet(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody LogWorkoutSetRequest request) {
        return ResponseEntity.ok(Map.of(
                "set", workoutLogService.saveSet(currentUser.getId(jwt), request)));
    }

    @PostMapping("/complete")
    public ResponseEntity<Map<String, WorkoutLogResponse>> complete(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CompleteWorkoutLogRequest request) {
        return ResponseEntity.ok(Map.of(
                "workoutLog", workoutLogService.complete(currentUser.getId(jwt), request.getWorkoutLogId())));
    }
}
