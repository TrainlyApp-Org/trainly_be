package com.trainly.backend.service;

import com.trainly.backend.entity.Exercise;
import com.trainly.backend.entity.Profile;
import com.trainly.backend.repository.ExerciseRepository;
import com.trainly.backend.repository.ProfileRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import com.trainly.backend.repository.CategoryRepository;
import com.trainly.backend.dto.ExerciseDto;
import com.trainly.backend.dto.CreateExerciseRequest;
import com.trainly.backend.entity.Category;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final CategoryRepository categoryRepository;
    private final ProfileRepository profileRepository;


    public ExerciseDto createCustomExercise(
            String name,
            UUID categoryId,
            String description,
            UUID userId
    ) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));


        Exercise exercise = Exercise.builder()
                .name(name)
                .category(category)
                .description(description)
                .isCustom(true)
                .createdBy(profile)
                .build();


        Exercise saved = exerciseRepository.save(exercise);

        return ExerciseDto.builder()
                .id(saved.getId())
                .name(saved.getName())
                .description(saved.getDescription())
                .categoryId(saved.getCategory().getId())
                .build();
    }

    public List<ExerciseDto> getExercisesForUser(UUID userId) {

        List<Exercise> exercises = exerciseRepository.findByIsCustomFalseOrCreatedById(userId);

        return exercises.stream()
                .map(this::mapToDto)
                .toList();
    }
    
    private ExerciseDto mapToDto(Exercise exercise) {

        return ExerciseDto.builder()
                .id(exercise.getId())
                .name(exercise.getName())
                .description(exercise.getDescription())
                .categoryId(exercise.getCategory().getId())
                .categoryName(exercise.getCategory().getName())
                .isCustom(exercise.isCustom())
                .build();
    }

    public ExerciseDto createCustomExercise(CreateExerciseRequest request, UUID userId) {

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));


        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));


        Exercise exercise = Exercise.builder()
                .name(request.getName())
                .category(category)
                .description(request.getDescription())
                .isCustom(true)
                .createdBy(profile)
                .build();


        Exercise savedExercise = exerciseRepository.save(exercise);


        return mapToDto(savedExercise);
    }
}