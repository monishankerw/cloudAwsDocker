package com.cloudAwsDocker.controller;

import com.cloudAwsDocker.dto.ApiResponse;
import com.cloudAwsDocker.dto.JwtAuthenticationResponse;
import com.cloudAwsDocker.dto.LoginRequest;
import com.cloudAwsDocker.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<JwtAuthenticationResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtAuthenticationResponse response = authService.authenticateUser(loginRequest);
        return new ApiResponse<>(
                true,
                "User authenticated successfully",
                response,
                200,
                java.time.LocalDateTime.now(),
                null
        );
    }
}
