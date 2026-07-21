package com.trainly.backend.repository;

import com.trainly.backend.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, UUID> {
    
    List<Exercise> findByCategoryId(UUID categoryId);

    List<Exercise> findByNameContainingIgnoreCase(String name);

    List<Exercise> findByCategoryIdAndNameContainingIgnoreCase(
            UUID categoryId,
            String name
    );

    List<Exercise> findByIsCustomTrueAndCreatedById(UUID profileId);

    List<Exercise> findByIsCustomFalseOrCreatedById(UUID userId);

    @Query("""
        SELECT e 
        FROM Exercise e
        WHERE e.isCustom = false
        OR e.createdBy = :userId
    """)
    List<Exercise> findAvailableExercises(
            @Param("userId") UUID userId
    );
}
