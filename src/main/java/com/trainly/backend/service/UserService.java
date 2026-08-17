package com.trainly.backend.service;

import com.trainly.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;
import java.time.OffsetDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RestClient restClient;
    private final String passwordResetRedirectUrl;

    public UserService(UserRepository userRepository, 
                       @Value("${supabase.url}") String supabaseUrl, 
                       @Value("${supabase.anon-key}") String supabaseAnonKey,
                       @Value("${app.frontend-url}") String frontendUrl) {
        this.userRepository = userRepository;
        this.passwordResetRedirectUrl = frontendUrl.replaceAll("/+$", "") + "/reset-password";
        this.restClient = RestClient.builder()
                .baseUrl(supabaseUrl + "/auth/v1")
                .defaultHeader("apikey", supabaseAnonKey)
                .defaultHeader("Authorization", "Bearer " + supabaseAnonKey)
                .build();
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> registerUser(
            String email,
            String password,
            String username,
            String fullName,
            boolean adultConfirmed,
            boolean termsAccepted,
            boolean privacyAcknowledged) {
        if (username != null && !username.trim().isEmpty() && userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists.");
        }

        // Payload for Supabase SignUp
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("email", email);
        requestBody.put("password", password);
        
        Map<String, Object> userMetadata = new HashMap<>();
        userMetadata.put(
                "username",
                username != null ? username : email.split("@")[0]
        );
        userMetadata.put(
                "full_name",
                fullName != null ? fullName : ""
        );
        userMetadata.put("adult_confirmed", adultConfirmed);
        userMetadata.put("terms_accepted", termsAccepted);
        userMetadata.put("privacy_acknowledged", privacyAcknowledged);
        userMetadata.put("legal_document_version", "1.0");
        userMetadata.put("legal_accepted_at", OffsetDateTime.now().toString());

        requestBody.put("data", userMetadata);

        try {
            Map<String, Object> supabaseResponse = restClient.post()
                    .uri("/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Registration successful! Check your email for verification if enabled.");
            if (supabaseResponse != null) {
                response.put("user", supabaseResponse.get("user"));
                response.put("session", supabaseResponse.get("session"));
            }
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> loginUser(String email, String password) {

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("email", email);
        requestBody.put("password", password);

        Map<String, Object> supabaseResponse =
                restClient.post()
                        .uri("/token?grant_type=password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(requestBody)
                        .retrieve()
                        .body(Map.class);


        Map<String, Object> response = new HashMap<>();

        response.put("message", "Login successful!");

        response.put(
            "access_token",
            supabaseResponse.get("access_token")
        );

        response.put(
            "refresh_token",
            supabaseResponse.get("refresh_token")
        );

        response.put(
            "expires_in",
            supabaseResponse.get("expires_in")
        );

        response.put(
            "user",
            supabaseResponse.get("user")
        );


        return response;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> refreshSession(String refreshToken) {
        Map<String, Object> supabaseResponse = restClient.post()
                .uri("/token?grant_type=refresh_token")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("refresh_token", refreshToken))
                .retrieve()
                .body(Map.class);

        if (supabaseResponse == null || supabaseResponse.get("access_token") == null) {
            throw new IllegalArgumentException("Unable to refresh session");
        }

        return supabaseResponse;
    }

    public void requestPasswordReset(String email) {
        restClient.post()
                .uri("/recover")
                .header("X-Supabase-Redirect-To", passwordResetRedirectUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("email", email.trim()))
                .retrieve()
                .toBodilessEntity();
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> resetPassword(String recoveryAccessToken, String newPassword) {
        Map<String, Object> response = restClient.put()
                .uri("/user")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + recoveryAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("password", newPassword))
                .retrieve()
                .body(Map.class);

        if (response == null) {
            throw new IllegalArgumentException("Unable to reset password");
        }
        return response;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> changePassword(
            String accessToken, String currentPassword, String newPassword) {
        if (currentPassword.equals(newPassword)) {
            throw new IllegalArgumentException("New password must be different from current password");
        }

        Map<String, Object> response = restClient.put()
                .uri("/user")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "current_password", currentPassword,
                        "password", newPassword
                ))
                .retrieve()
                .body(Map.class);

        if (response == null) {
            throw new IllegalArgumentException("Unable to change password");
        }
        return response;
    }
}
