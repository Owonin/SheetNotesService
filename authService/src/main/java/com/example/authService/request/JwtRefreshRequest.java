package com.example.authService.request;

import lombok.Data;

@Data
public class JwtRefreshRequest {
    public String refreshToken;
}
