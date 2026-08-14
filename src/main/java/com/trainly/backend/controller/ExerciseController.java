package com.trainly.backend.controller;

import com.trainly.backend.dto.ExerciseDto;
import com.trainly.backend.security.CurrentUser;
import com.trainly.backend.dto.CreateExerciseRequest;
import com.trainly.backend.service.ExerciseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/exercises")
@RequiredArgsConstructor
public class ExerciseController {


    private final ExerciseService exerciseService;
    private final CurrentUser currentUser;



    /**
     * Recupera esercizi disponibili:
     * - esercizi globali
     * - esercizi custom dell'utente
     */
    @GetMapping()
    public ResponseEntity<Map<String,List<ExerciseDto>>> getExercises(
            @AuthenticationPrincipal Jwt jwt
    ) {

        UUID userId = currentUser.getId(jwt);


        List<ExerciseDto> exercises =
                exerciseService.getExercisesForUser(userId);


        return ResponseEntity.ok(
                Map.of(
                        "exercises", exercises
                )
        );
    }




    /**
     * Crea esercizio personalizzato
     */
    @PostMapping
    public ResponseEntity<Map<String,Object>> createCustomExercise(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateExerciseRequest request
    ) {
        UUID userId = currentUser.getId(jwt);

        ExerciseDto exercise =
                exerciseService.createCustomExercise(
                        request,
                        userId
                );


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                    Map.of(
                        "message",
                        "Custom exercise created successfully",
                        "exercise",
                        exercise
                    )
                );
    }

}
