package com.example.authService.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class JwtResponse {
    public JwtResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    private String accessToken;
    private String refreshToken;
    private String type = "Bearer ";
}
