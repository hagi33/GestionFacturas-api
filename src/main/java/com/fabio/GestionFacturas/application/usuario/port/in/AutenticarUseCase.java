package com.fabio.GestionFacturas.application.usuario.port.in;

public interface AutenticarUseCase {


    ResultadoAutenticacion autenticar(ComandoLogin comandoLogin);

    record ComandoLogin(String email, String password){};
    record ResultadoAutenticacion(String accessToken, String refreshToken, Long usuarioId, String email){};

}
