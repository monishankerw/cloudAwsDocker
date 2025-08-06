package com.cloudAwsDocker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthenticationResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    
    public JwtAuthenticationResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
