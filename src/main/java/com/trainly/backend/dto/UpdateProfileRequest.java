package com.trainly.backend.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequest {
    @Size(max = 100)
    private String username;

    @Size(max = 200)
    private String fullName;

    @Size(max = 2048)
    private String avatarUrl;
}
