package com.example.authService.config;

import com.example.authService.entity.UserDetailsImpl;
import com.example.authService.entity.UserEntity;
import com.example.authService.repository.UserRepository;
import com.example.authService.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@RequiredArgsConstructor
@Component
public class JwtAthFilter extends OncePerRequestFilter {


    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader(AUTHORIZATION);
        final String jwtToken;

        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwtToken = authHeader.substring(7);
        String userLogin = jwtUtils.extractAccessTokenUsername(jwtToken);
        if (userLogin != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserEntity user = userRepository.findByLogin(userLogin)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            UserDetails userDetails = UserDetailsImpl.build(user);

            final boolean isTokenValid = jwtUtils.validateAccessToken(jwtToken);
            if (isTokenValid) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }


}
