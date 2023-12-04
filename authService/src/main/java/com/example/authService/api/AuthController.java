package com.example.authService.api;

import com.example.authService.dto.UserDto;
import com.example.authService.mapper.UserMapper;
import com.example.authService.request.AuthenticationRequest;
import com.example.authService.request.JwtRefreshRequest;
import com.example.authService.response.JwtResponse;
import com.example.authService.response.UserValidationResponse;
import com.example.authService.service.AuthService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("${api.url}")
@Slf4j
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary = "Контроллер прав пользователя",
            description = "Проверяет права авторизации пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserValidationResponse.class)
                    )})
    })
    @GetMapping
    public ResponseEntity<Object> getUserAuthority() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserValidationResponse user = new UserValidationResponse(auth.getName(),
                auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());

        return ResponseEntity.ok().body(user);
    }

    @Operation(
            summary = "Контроллер генерации токенов",
            description = "Генерирует access и refresh токены для зарегестрированного пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = JwtResponse.class)
                    )}),
    })
    @PostMapping
    public ResponseEntity<Object> authenticate(@RequestBody AuthenticationRequest request) {
        JwtResponse jwtResponse = this.authService.createToken(request);
        return ResponseEntity.ok().body(jwtResponse);
    }

    @Operation(
            summary = "Контроллер регистрации",
            description = "Регестрирует пользователя в системе"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Login in already exists",
                    content = @Content)
    })
    @PostMapping({"/reg"})
    public ResponseEntity<?> newUser(@RequestBody UserDto user) {
        this.authService.singUp(UserMapper.userDtoToUserEntity(user),false);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Контроллер регистрации администратора",
            description = "Регестрирует администратора в системе"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Login in already exists",
                    content = @Content)
    })
    @PostMapping({"/admin"})
    public ResponseEntity<?> newAdminUser(@RequestBody UserDto user) {
        this.authService.singUp(UserMapper.userDtoToUserEntity(user),true);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Контроллер обновления токенов",
            description = "Обнавляет access и refresh токены"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = JwtResponse.class)
                    )}),
            @ApiResponse(responseCode = "403", description = "Jwt refresh token expired",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User is not found",
                    content = @Content)
    })
    @PostMapping({"/refresh"})
    public ResponseEntity<JwtResponse> refresh(@RequestBody JwtRefreshRequest refresh) {
        JwtResponse token = this.authService.refresh(refresh.getRefreshToken());
        return ResponseEntity.ok().body(token);
    }

    @Hidden
    @GetMapping("/health")
    public ResponseEntity<String> getAuthStatus() {

        return ResponseEntity.ok().body("Auth service is active");
    }
}

