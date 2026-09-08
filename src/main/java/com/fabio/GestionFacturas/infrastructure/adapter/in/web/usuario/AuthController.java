package com.fabio.GestionFacturas.infrastructure.adapter.in.web.usuario;


import com.fabio.GestionFacturas.application.usuario.port.in.AutenticarUseCase;
import com.fabio.GestionFacturas.application.usuario.port.in.AutenticarUseCase.*;
import com.fabio.GestionFacturas.application.usuario.port.in.AutenticarUseCase.ResultadoAutenticacion;
import com.fabio.GestionFacturas.application.usuario.port.in.RegistrarUsuarioUseCase;
import com.fabio.GestionFacturas.application.usuario.port.in.RegistrarUsuarioUseCase.*;
import com.fabio.GestionFacturas.application.usuario.service.AutenticarService;
import com.fabio.GestionFacturas.domain.usuario.Usuario;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.usuario.dto.LoginRequest;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.usuario.dto.LoginResponse;
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

    public AuthController(RegistrarUsuarioUseCase registrarUsuarioUseCase,
                          AutenticarService autenticarService) {
        this.registrarUsuarioUseCase = registrarUsuarioUseCase;
        this.autenticarService = autenticarService;
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
                resultadoAutenticacion.accessToken(), resultadoAutenticacion.usuarioId(), resultadoAutenticacion.email()
        );

        return ResponseEntity.ok(loginResponse);

    }




}
