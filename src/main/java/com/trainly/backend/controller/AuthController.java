package com.trainly.backend.controller;

import com.trainly.backend.dto.LoginRequestDto;
import com.trainly.backend.dto.RegisterDto;
import com.trainly.backend.dto.ProfileResponse;
import com.trainly.backend.dto.UpdateProfileRequest;
import com.trainly.backend.dto.RefreshTokenRequest;
import com.trainly.backend.dto.ChangePasswordRequest;
import com.trainly.backend.security.CurrentUser;
import com.trainly.backend.service.ProfileService;
import com.trainly.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.RestClientResponseException;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final ProfileService profileService;
    private final CurrentUser currentUser;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDto request) {
        try {
            Map<String, Object> result = userService.registerUser(
                    request.getEmail(),
                    request.getPassword(),
                    request.getUsername(),
                    request.getFullName()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto request) {
        try {
            Map<String, Object> result = userService.loginUser(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(result);
        } catch (RestClientResponseException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenziali errate"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenziali errate"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Impossibile completare l'accesso"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            return ResponseEntity.ok(userService.refreshSession(request.getRefreshToken()));
        } catch (RestClientResponseException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Refresh token expired or invalid"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, ProfileResponse>> getProfile(@AuthenticationPrincipal Jwt jwt) {
        ProfileResponse profile = profileService.getProfileResponse(currentUser.getId(jwt));
        return ResponseEntity.ok(Map.of("profile", profile));
    }

    @PutMapping("/me")
    public ResponseEntity<Map<String, ProfileResponse>> updateProfile(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateProfileRequest request) {
        ProfileResponse profile = profileService.updateProfile(currentUser.getId(jwt), request);
        return ResponseEntity.ok(Map.of("profile", profile));
    }

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ChangePasswordRequest request) {
        try {
            userService.changePassword(
                    jwt.getTokenValue(), request.getCurrentPassword(), request.getNewPassword());
            return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
        } catch (RestClientResponseException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Current password is incorrect or the new password is not valid"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

}
