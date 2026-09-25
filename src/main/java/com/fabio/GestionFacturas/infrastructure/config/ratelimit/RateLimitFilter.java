package com.fabio.GestionFacturas.infrastructure.config.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> bucketsPorIpLogin = new ConcurrentHashMap<>();
    private final Map<String, Bucket> bucketsPorIpRegister = new ConcurrentHashMap<>();
    private final Map<String, Bucket> bucketsPorUsuario = new ConcurrentHashMap<>();


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String ruta = request.getRequestURI();
        String ip = request.getRemoteAddr();


        Bucket bucket = null;

        if (ruta.equals("/api/auth/login")){
            bucket = bucketsPorIpLogin.computeIfAbsent(ip, clave -> crearBucketLogin());
        }
        if (ruta.equals("/api/auth/register")){
            bucket = bucketsPorIpRegister.computeIfAbsent(ip, c -> crearBucketRegistro());
        }

       boolean esRutaAuth =  ruta.equals("/api/auth/login") || ruta.equals("/api/auth/register");
        if (!esRutaAuth){
            Long usuarioId = obtenerUsuarioAutenticado();
            if (usuarioId != null){
                String clave = usuarioId.toString();
                bucket = bucketsPorUsuario.computeIfAbsent(clave, c -> crearBucketUsuario());
            }
        }

        //Si la ruta no es de las que están limitadas, el bucket sigue con valor null y la deja pasar sin limitaciones
        if (bucket == null){
            filterChain.doFilter(request, response);
            return;
        }

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
            return;
        }

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setHeader("Retry-After", "60");
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"Demasiadas peticiones. Inténtalo más tarde.\"}");
    }

    private Long obtenerUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        // JwtAuthenticationFilter stores the user id (Long) as principal; anonymous is a String
        if (auth.getPrincipal() instanceof Long usuarioId) {
            return usuarioId;
        }
        return null;
    }

    private Bucket crearBucketUsuario(){
        Bandwidth limite = Bandwidth.builder()
                .capacity(50)
                .refillGreedy(50,Duration.ofMinutes(1))
                .build();
        return Bucket.builder().addLimit(limite).build();
    }

    private Bucket crearBucketLogin(){
        Bandwidth limite = Bandwidth.builder()
                .capacity(10)
                .refillGreedy(10, Duration.ofMinutes(1))
                .build();
        return Bucket.builder().addLimit(limite).build();
    }

    private Bucket crearBucketRegistro(){
        Bandwidth limite = Bandwidth.builder()
                .capacity(5)
                .refillGreedy(5, Duration.ofHours(1))
                .build();
        return Bucket.builder().addLimit(limite).build();
    }



}
