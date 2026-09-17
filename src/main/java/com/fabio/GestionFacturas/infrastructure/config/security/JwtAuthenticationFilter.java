package com.fabio.GestionFacturas.infrastructure.config.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Runs once per request (before it reaches any controller), reading the Bearer token and,
 * if valid, populating the {@code SecurityContext} so {@code @AuthenticationPrincipal Long usuarioId}
 * works in controllers. Registered in {@link com.fabio.GestionFacturas.infrastructure.config.SecurityConfig}
 * ahead of Spring's default filter. No token, or an invalid one, just falls through unauthenticated —
 * it's {@code anyRequest().authenticated()} in SecurityConfig that then rejects the request.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;


    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }

        String token = authHeader.substring(7);

        if (!jwtTokenProvider.esValido(token)){
            filterChain.doFilter(request, response);
            return;
        }

        Long usuarioId = jwtTokenProvider.extraerUsuarioId(token);

        // The principal is just the user id (Long) — no roles/authorities used in this app, hence List.of()
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                usuarioId, null, List.of());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);


    }
}
