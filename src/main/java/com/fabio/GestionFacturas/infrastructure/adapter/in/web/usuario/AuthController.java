package com.fabio.GestionFacturas.infrastructure.adapter.in.web.usuario;


import com.fabio.GestionFacturas.application.usuario.port.in.AutenticarUseCase;
import com.fabio.GestionFacturas.application.usuario.port.in.AutenticarUseCase.*;
import com.fabio.GestionFacturas.application.usuario.port.in.AutenticarUseCase.ResultadoAutenticacion;
import com.fabio.GestionFacturas.application.usuario.port.in.LogoutUseCase;
import com.fabio.GestionFacturas.application.usuario.port.in.LogoutUseCase.*;
import com.fabio.GestionFacturas.application.usuario.port.in.RefreshTokenUseCase;
import com.fabio.GestionFacturas.application.usuario.port.in.RefreshTokenUseCase.*;
import com.fabio.GestionFacturas.application.usuario.port.in.RegistrarUsuarioUseCase;
import com.fabio.GestionFacturas.application.usuario.port.in.RegistrarUsuarioUseCase.*;
import com.fabio.GestionFacturas.application.usuario.service.AutenticarService;
import com.fabio.GestionFacturas.domain.usuario.Usuario;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.usuario.dto.LoginRequest;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.usuario.dto.LoginResponse;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.usuario.dto.LogoutRequest;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.usuario.dto.RefreshRequest;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.usuario.dto.RefreshResponse;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.usuario.dto.RegistrarRequest;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.usuario.dto.UsuarioResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Web adapter (inbound) for the full auth workflow: register -> login -> refresh -> logout.
 * Each method maps a request DTO straight to the matching inbound-port command, delegates to
 * the port, then maps the result back to a response DTO — auth logic itself lives entirely
 * in the application-layer services behind these ports (never here).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;
    // Depends on the concrete service here instead of AutenticarUseCase — a small deviation from
    // the "controllers depend only on ports" rule elsewhere in this codebase.
    private final AutenticarService autenticarService;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;

    public AuthController(RegistrarUsuarioUseCase registrarUsuarioUseCase,
                          AutenticarService autenticarService,
                          RefreshTokenUseCase refreshTokenUseCase,
                          LogoutUseCase logoutUseCase) {
        this.registrarUsuarioUseCase = registrarUsuarioUseCase;
        this.autenticarService = autenticarService;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.logoutUseCase = logoutUseCase;
    }


    // request -> ComandoRegistrar -> RegistrarUsuarioUseCase -> RegistrarUsuarioService (hashes password) -> Usuario
    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistrarRequest request){
        ComandoRegistrar comandoRegistrar = new ComandoRegistrar(request.email(), request.nombre(), request.password());

        Usuario usuario = registrarUsuarioUseCase.registrar(comandoRegistrar);

        UsuarioResponse usuarioResponse = new UsuarioResponse(usuario.getId(), usuario.getEmail(), usuario.getNombre());

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioResponse);

    }

    // request -> ComandoLogin -> AutenticarService: verifies password, issues access JWT + refresh token
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request){
        ComandoLogin comandoRegistrar = new ComandoLogin(request.email(),request.password());

        ResultadoAutenticacion resultadoAutenticacion = autenticarService.autenticar(comandoRegistrar);

        LoginResponse loginResponse = new LoginResponse(
                resultadoAutenticacion.accessToken(), resultadoAutenticacion.refreshToken(),
                resultadoAutenticacion.usuarioId(), resultadoAutenticacion.email()
        );

        return ResponseEntity.ok(loginResponse);

    }

    // request -> ComandoRenovar -> RefreshTokenUseCase: validates the stored hash, issues a new access JWT only
    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(@Valid @RequestBody RefreshRequest request){
        ComandoRenovar comandoRenovar = new ComandoRenovar(request.refreshToken());

        ResultadoRenovacion resultadoRenovacion = refreshTokenUseCase.renovar(comandoRenovar);

        RefreshResponse refreshResponse = new RefreshResponse(resultadoRenovacion.accessToken());

        return ResponseEntity.ok(refreshResponse);
    }

    // request -> ComandoLogout -> LogoutUseCase: revokes the refresh token (row kept, not deleted)
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request){
        ComandoLogout comandoLogout = new ComandoLogout(request.refreshToken());

        logoutUseCase.logout(comandoLogout);

        return ResponseEntity.noContent().build();
    }




}
