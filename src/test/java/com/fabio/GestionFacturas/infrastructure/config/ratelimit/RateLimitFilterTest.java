package com.fabio.GestionFacturas.infrastructure.config.ratelimit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.*;


/**
 * Unit tests for RateLimitFilter, constructed directly (no Spring context). The SecurityContext is
 * populated the same way JwtAuthenticationFilter does it (principal = Long user id), so these tests
 * guard the per-user limit on authenticated routes as well as the per-IP limits on login/register.
 * Buckets are private, so everything is asserted through the HTTP status the filter produces.
 */
class RateLimitFilterTest {

    private static final String IP_A = "10.0.0.1";
    private static final String IP_B = "10.0.0.2";

    private static final int LIMITE_USUARIO = 50;
    private static final int LIMITE_LOGIN = 10;
    private static final int LIMITE_REGISTRO = 5;

    private final RateLimitFilter filter = new RateLimitFilter();


    @AfterEach
    void limpiarContexto(){
        SecurityContextHolder.clearContext();
    }


    @Test
    @DisplayName("un usuario autenticado puede hacer 50 peticiones por minuto y la 51 se rechaza con 429")
    void limitaPorUsuarioEnRutasAutenticadas() throws Exception {
        autenticarComo(42L);

        agotar(LIMITE_USUARIO, "GET", "/api/gastos", IP_A);

        assertThat(ejecutar("GET", "/api/gastos", IP_A).getStatus()).isEqualTo(429);
    }

    @Test
    @DisplayName("cada usuario tiene su propio bucket aunque compartan IP")
    void bucketsSeparadosPorUsuarioYNoPorIp() throws Exception {
        autenticarComo(42L);
        agotar(LIMITE_USUARIO, "GET", "/api/gastos", IP_A);
        assertThat(ejecutar("GET", "/api/gastos", IP_A).getStatus()).isEqualTo(429);

        autenticarComo(43L);

        assertThat(ejecutar("GET", "/api/gastos", IP_A).getStatus()).isEqualTo(200);
    }

    @Test
    @DisplayName("la respuesta 429 lleva Retry-After, JSON de error y no continúa la cadena")
    void respuesta429NoContinuaLaCadena() throws Exception {
        autenticarComo(42L);
        agotar(LIMITE_USUARIO, "GET", "/api/gastos", IP_A);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/gastos");
        request.setRemoteAddr(IP_A);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(429);
        assertThat(response.getHeader("Retry-After")).isEqualTo("60");
        assertThat(response.getContentType()).startsWith("application/json");
        assertThat(response.getContentAsString()).contains("\"error\"");
        assertThat(chain.getRequest()).isNull();
    }

    @Test
    @DisplayName("las peticiones sin autenticar a rutas no-auth no se limitan en este filtro")
    void peticionesSinAutenticarNoSeLimitan() throws Exception {
        agotar(LIMITE_USUARIO + 10, "GET", "/api/health", IP_A);
    }

    @Test
    @DisplayName("el usuario anónimo de Spring Security (principal String) no se limita en este filtro")
    void usuarioAnonimoNoSeLimita() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken(
                "clave", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")));

        agotar(LIMITE_USUARIO + 10, "GET", "/api/gastos", IP_A);
    }

    @Test
    @DisplayName("el login se limita a 10 peticiones por IP y otra IP no se ve afectada")
    void limitaLoginPorIp() throws Exception {
        agotar(LIMITE_LOGIN, "POST", "/api/auth/login", IP_A);

        assertThat(ejecutar("POST", "/api/auth/login", IP_A).getStatus()).isEqualTo(429);
        assertThat(ejecutar("POST", "/api/auth/login", IP_B).getStatus()).isEqualTo(200);
    }

    @Test
    @DisplayName("el registro se limita a 5 peticiones por IP")
    void limitaRegistroPorIp() throws Exception {
        agotar(LIMITE_REGISTRO, "POST", "/api/auth/register", IP_A);

        assertThat(ejecutar("POST", "/api/auth/register", IP_A).getStatus()).isEqualTo(429);
    }

    @Test
    @DisplayName("las peticiones de login no consumen el bucket del usuario")
    void loginNoConsumeBucketDelUsuario() throws Exception {
        autenticarComo(42L);
        agotar(LIMITE_LOGIN, "POST", "/api/auth/login", IP_A);

        agotar(LIMITE_USUARIO, "GET", "/api/gastos", IP_A);
    }


    /** Sends {@code veces} requests and asserts every one of them gets through (200). */
    private void agotar(int veces, String metodo, String ruta, String ip) throws Exception {
        for (int i = 1; i <= veces; i++) {
            assertThat(ejecutar(metodo, ruta, ip).getStatus())
                    .as("petición %d a %s", i, ruta)
                    .isEqualTo(200);
        }
    }

    private MockHttpServletResponse ejecutar(String metodo, String ruta, String ip) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(metodo, ruta);
        request.setRemoteAddr(ip);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        return response;
    }

    // Same principal shape JwtAuthenticationFilter puts in the SecurityContext
    private void autenticarComo(Long usuarioId){
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuarioId, null, List.of()));
    }
}
