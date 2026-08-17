package com.trainly.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
    @NotBlank(message = "Recovery token is required")
    private String accessToken;

    @NotBlank(message = "New password is required")
    @Size(min = 6, max = 72, message = "Password must be between 6 and 72 characters")
    private String newPassword;
}
