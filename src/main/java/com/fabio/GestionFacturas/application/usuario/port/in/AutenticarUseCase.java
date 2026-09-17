package com.fabio.GestionFacturas.application.usuario.port.in;

/** Inbound port for login. Implemented by {@code AutenticarService}, which verifies credentials and issues tokens. */
public interface AutenticarUseCase {


    ResultadoAutenticacion autenticar(ComandoLogin comandoLogin);

    record ComandoLogin(String email, String password){};
    /** accessToken is a self-contained JWT (never stored); refreshToken is returned here in plaintext once — only its hash is persisted. */
    record ResultadoAutenticacion(String accessToken, String refreshToken, Long usuarioId, String email){};

}
