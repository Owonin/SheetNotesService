package com.example.authService.service;

import com.example.authService.entity.*;
import com.example.authService.exception.AuthException;
import com.example.authService.repository.JwtRepository;
import com.example.authService.repository.RoleRepository;
import com.example.authService.repository.UserRepository;
import com.example.authService.request.AuthenticationRequest;
import com.example.authService.response.JwtResponse;
import com.example.authService.util.JwtUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@Slf4j
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtRepository jwtRepository;
    private final JwtUtils jwtUtils;


    public void singUp(UserEntity user, boolean isAdmin) {
        String id = UUID.randomUUID().toString().replace("-", "");
        user.setId(id);

        log.info("New user {} is trying to sing up", user.getLogin());

        if (userRepository.existsByLogin(user.getLogin()))
            throw new AuthException(HttpStatus.CONFLICT, "This login is already in use");

        Set<RoleEntity> roles = new HashSet<>();
        roles.add(roleRepository.findByName(ERoles.ROLE_USER).get());

        if (isAdmin) {
            roles.add(roleRepository.findByName(ERoles.ROLE_ADMIN).get());
        }

        user.setRoles(roles);
        userRepository.save(user);

        log.info("New user {} has been successfully registered", user.getLogin());

    }

    public JwtResponse createToken(AuthenticationRequest request) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getLogin()
                        , request.getPassword()));
        UserDetails user = (UserDetails) authentication.getPrincipal();

        log.info("New user {} is trying to sing in", request.getLogin());

        if (user != null) {

            var accessToken = jwtUtils.generateAccessToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(user);

            var savedToken = jwtRepository.findByUsername(user.getUsername()).orElse(null);
            if (savedToken != null)
                jwtRepository.delete(savedToken);

            cacheToken(request.getLogin(), refreshToken);

            log.info("New access token {} and refresh token {} for user {} has been made", accessToken, refreshToken, user.getUsername());

            return new JwtResponse(accessToken, refreshToken);
        }

        log.warn("User {} has not been found", request.getLogin());

        throw new AuthException(HttpStatus.NOT_FOUND, "User " + request.getLogin() + " has not found");
    }

    private void cacheToken(String login, String refreshToken) {
        JwtEntity jwtEntity = new JwtEntity();
        jwtEntity.setId(UUID.randomUUID().toString().substring(0, 8));
        jwtEntity.setAuthenticationToken(refreshToken);
        jwtEntity.setUsername(login);

        jwtRepository.save(jwtEntity);
    }

    public JwtResponse refresh(String refreshToken) {


        var username = jwtUtils.extractRefreshTokenUsername(refreshToken);

        if (jwtUtils.validateRefreshToken(refreshToken)) {


            var savedToken = jwtRepository.findByUsername(username).orElse(null);
            var user = userRepository.findByLogin(username)
                    .orElseThrow(() -> new AuthException(HttpStatus.NOT_FOUND, "User is not found"));
            UserDetails userDetails = UserDetailsImpl.build(user);

            if (savedToken != null && savedToken.getAuthenticationToken().equals(refreshToken)) {

                var accessToken = jwtUtils.generateAccessToken(userDetails);
                var newRefreshToken = jwtUtils.generateRefreshToken(userDetails);

                jwtRepository.delete(savedToken);

                cacheToken(username, refreshToken);

                return new JwtResponse(accessToken, newRefreshToken);
            }
        }

        throw new AuthException(HttpStatus.FORBIDDEN, "Jwt token expired");
    }
}
