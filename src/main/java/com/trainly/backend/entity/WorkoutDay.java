package com.trainly.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="workout_days")
public class WorkoutDay {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_plan_id")
    @JsonIgnore
    private WorkoutPlan workoutPlan;

    private String name;

    private Integer orderIndex;

    @OneToMany(mappedBy = "workoutDay",
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<WorkoutDayExercise> exercises = new ArrayList<>();
}