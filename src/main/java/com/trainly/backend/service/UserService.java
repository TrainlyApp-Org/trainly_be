package com.trainly.backend.service;

import com.trainly.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RestClient restClient;

    public UserService(UserRepository userRepository, 
                       @Value("${supabase.url}") String supabaseUrl, 
                       @Value("${supabase.anon-key}") String supabaseAnonKey) {
        this.userRepository = userRepository;
        this.restClient = RestClient.builder()
                .baseUrl(supabaseUrl + "/auth/v1")
                .defaultHeader("apikey", supabaseAnonKey)
                .defaultHeader("Authorization", "Bearer " + supabaseAnonKey)
                .build();
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> registerUser(String email, String password, String username, String fullName) {
        if (username != null && !username.trim().isEmpty() && userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists.");
        }

        // Payload for Supabase SignUp
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("email", email);
        requestBody.put("password", password);
        
        Map<String, String> userMetadata = new HashMap<>();
        userMetadata.put(
                "username",
                username != null ? username : email.split("@")[0]
        );
        userMetadata.put(
                "full_name",
                fullName != null ? fullName : ""
        );

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
}
