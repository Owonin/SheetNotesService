package com.example.notesService.config;

import com.example.notesService.entity.UserDetailsImpl;
import com.example.notesService.dto.UserDto;
import com.example.notesService.exception.AuthServerException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    private String authServerUrl;
    private UserDetailsImpl userDetails;

    public JwtTokenFilter(String authServerUrl) {
        this.authServerUrl = authServerUrl;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader(AUTHORIZATION);
        final String jwtToken;

        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            filterChain.doFilter(request, response);

            return;
        }

        jwtToken = authHeader.substring(7);

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            if (validateTokenWithAuthServer(jwtToken)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean validateTokenWithAuthServer(String token) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        String authServerApiUrl = authServerUrl;
        ResponseEntity<UserDto> responseEntity = null;

        try {
            responseEntity = restTemplate.exchange(
                    authServerApiUrl,
                    HttpMethod.GET,
                    entity,
                    UserDto.class
            );

        } catch (IllegalArgumentException e) {
            String errorMessage = "Auth server is not available";
            log.error(errorMessage);

            throw new AuthServerException(errorMessage);
        }

        if (Objects.requireNonNull(responseEntity).getStatusCode().is2xxSuccessful()) {
            UserDto userEntity = responseEntity.getBody();
            userDetails = UserDetailsImpl.build(Objects.requireNonNull(userEntity));

            return true;
        }

        return false;
    }
}
