package com.example.authService.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserValidationResponse {
    private String login;
    private List<String> roles;
}
