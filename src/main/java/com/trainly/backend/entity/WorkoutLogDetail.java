package com.trainly.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "workout_log_details", schema = "public",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_workout_log_set",
                columnNames = {"workout_log_id", "exercise_id", "set_index"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutLogDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workout_log_id", nullable = false)
    private WorkoutLog workoutLog;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(name = "set_index", nullable = false)
    private Integer setIndex;

    @Column(nullable = false)
    private Integer reps;

    @Column(nullable = false)
    private BigDecimal weight;

    @Column(nullable = false)
    private boolean completed;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (weight == null) weight = BigDecimal.ZERO;
        if (createdAt == null) createdAt = OffsetDateTime.now();
    }
}
