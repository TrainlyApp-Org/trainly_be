package com.trainly.backend.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.AssertTrue;

@Data
public class RegisterDto {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private String username;

    private String fullName;

    @AssertTrue(message = "You must be at least 18 years old")
    private boolean adultConfirmed;

    @AssertTrue(message = "Terms and conditions must be accepted")
    private boolean termsAccepted;

    @AssertTrue(message = "Privacy notice must be acknowledged")
    private boolean privacyAcknowledged;
}
