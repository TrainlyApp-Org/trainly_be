package com.trainly.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.trainly.backend.entity.WorkoutPlan;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutPlanDetailsResponse {

    private UUID id;

    private String name;

    private String description;

    private String creatorName;

    private UUID shareId;

    private Instant createdAt;

    private List<WorkoutDayResponse> days;

    public static WorkoutPlanDetailsResponse mapToDetailsResponse(WorkoutPlan workoutPlan)
    {

            WorkoutPlanDetailsResponse response =
                    new WorkoutPlanDetailsResponse();


            response.setId(workoutPlan.getId());
            response.setName(workoutPlan.getName());
            response.setDescription(workoutPlan.getDescription());
            String creatorName = workoutPlan.getProfile().getFullName();
            if (creatorName == null || creatorName.isBlank()) {
                    creatorName = workoutPlan.getProfile().getUsername();
            }
            response.setCreatorName(creatorName);
            response.setShareId(workoutPlan.getShareId());
            response.setCreatedAt(workoutPlan.getCreatedAt());


            List<WorkoutDayResponse> days =
                    workoutPlan.getDays()
                    .stream()
                    .map(day -> {

                            WorkoutDayResponse dayResponse =
                                    new WorkoutDayResponse();


                            dayResponse.setId(day.getId());
                            dayResponse.setName(day.getName());
                            dayResponse.setDayOrder(day.getOrderIndex());


                            List<WorkoutExerciseResponse> exercises =
                                    day.getExercises()
                                    .stream()
                                    .map(item -> {

                                    WorkoutExerciseResponse exercise =
                                            new WorkoutExerciseResponse();


                                    exercise.setId(item.getId());

                                    exercise.setExerciseId(
                                            item.getExercise().getId()
                                    );

                                    exercise.setName(
                                            item.getExercise().getDisplayName()
                                    );

                                    exercise.setDescription(
                                            item.getExercise().getDisplayDescription()
                                    );

                                    exercise.setSets(
                                            item.getSets()
                                    );

                                    exercise.setReps(
                                            item.getReps()
                                    );

                                    exercise.setRestTime(
                                            item.getRestTime()
                                    );


                                    return exercise;

                                    })
                                    .toList();


                            dayResponse.setExercises(exercises);

                            return dayResponse;

                    })
                    .toList();


            response.setDays(days);


            return response;
    }
}
