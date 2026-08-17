package com.trainly.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "exercises", schema = "public")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Exercise {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Column(nullable = false)
    private String name;

    @Column(name = "name_it")
    private String nameIt;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonIgnore
    private Category category;


    private String description;

    @Column(name = "description_it")
    private String descriptionIt;


    @Builder.Default
    @Column(name = "is_custom", nullable = false)
    private boolean isCustom = false;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Profile createdBy;


    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;


    @OneToMany(mappedBy = "exercise")
    @Builder.Default
    private List<WorkoutDayExercise> workoutDayExercises = new ArrayList<>();

    @Transient
    public String getDisplayName() {
        return nameIt != null && !nameIt.isBlank() ? nameIt : name;
    }

    @Transient
    public String getDisplayDescription() {
        return descriptionIt != null && !descriptionIt.isBlank() ? descriptionIt : description;
    }


}
