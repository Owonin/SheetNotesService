package com.example.authService.entity;

import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "Tokens", timeToLive = 432000)
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class JwtEntity {

    private String id;
    private String username;
    private String authenticationToken;
}
