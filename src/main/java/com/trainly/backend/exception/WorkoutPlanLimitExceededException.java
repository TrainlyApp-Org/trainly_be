package com.trainly.backend.exception;

public class WorkoutPlanLimitExceededException extends RuntimeException {
    public WorkoutPlanLimitExceededException() {
        super("Gli utenti non premium possono creare al massimo 5 schede di allenamento");
    }
}
