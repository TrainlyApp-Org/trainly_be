package com.trainly.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "shared_workout_sets", schema = "public",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_shared_workout_set",
                columnNames = {"share_id", "workout_day_id", "exercise_id", "set_index"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedWorkoutSet {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "share_id", nullable = false)
    private UUID shareId;

    @Column(name = "workout_day_id", nullable = false)
    private UUID workoutDayId;

    @Column(name = "exercise_id", nullable = false)
    private UUID exerciseId;

    @Column(name = "set_index", nullable = false)
    private Integer setIndex;

    @Column(nullable = false)
    private BigDecimal weight;

    @Column(nullable = false)
    private Integer reps;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
