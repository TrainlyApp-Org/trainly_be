package com.trainly.backend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AdminAccess {
    private final Set<String> adminUserIds;

    public AdminAccess(@Value("${app.admin-user-ids:}") String configuredIds) {
        this.adminUserIds = Arrays.stream(configuredIds.split(","))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .collect(Collectors.toUnmodifiableSet());
    }

    public boolean isAdmin(Jwt jwt) {
        return jwt != null && adminUserIds.contains(jwt.getSubject());
    }

    public void requireAdmin(Jwt jwt) {
        if (!isAdmin(jwt)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Administrator access required");
        }
    }
}
