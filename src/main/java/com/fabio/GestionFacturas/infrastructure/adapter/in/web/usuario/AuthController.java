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

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;
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


    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistrarRequest request){
        ComandoRegistrar comandoRegistrar = new ComandoRegistrar(request.email(), request.nombre(), request.password());

        Usuario usuario = registrarUsuarioUseCase.registrar(comandoRegistrar);

        UsuarioResponse usuarioResponse = new UsuarioResponse(usuario.getId(), usuario.getEmail(), usuario.getNombre());

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioResponse);

    }

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

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(@Valid @RequestBody RefreshRequest request){
        ComandoRenovar comandoRenovar = new ComandoRenovar(request.refreshToken());

        ResultadoRenovacion resultadoRenovacion = refreshTokenUseCase.renovar(comandoRenovar);

        RefreshResponse refreshResponse = new RefreshResponse(resultadoRenovacion.accessToken());

        return ResponseEntity.ok(refreshResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request){
        ComandoLogout comandoLogout = new ComandoLogout(request.refreshToken());

        logoutUseCase.logout(comandoLogout);

        return ResponseEntity.noContent().build();
    }




}
