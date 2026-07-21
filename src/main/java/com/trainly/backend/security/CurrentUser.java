package com.trainly.backend.security;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CurrentUser {


    public UUID getId(Jwt jwt){

        return UUID.fromString(
                jwt.getSubject()
        );

    }
}
