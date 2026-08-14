package com.trainly.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "workout_set_values", schema = "public",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_workout_set_value",
                columnNames = {"profile_id", "workout_plan_id", "workout_day_id", "exercise_id", "set_index"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutSetValue {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workout_plan_id", nullable = false)
    private WorkoutPlan workoutPlan;

    @Column(name = "workout_day_id", nullable = false)
    private UUID workoutDayId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(name = "set_index", nullable = false)
    private Integer setIndex;

    @Column(nullable = false)
    private BigDecimal weight;

    @Column(nullable = false)
    private Integer reps;
}
