package com.example.authService.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;


public class AuthException extends ResponseStatusException {
    public AuthException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}
