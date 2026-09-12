package com.fabio.GestionFacturas.application.usuario.port.in;

public interface RefreshTokenUseCase {

    ResultadoRenovacion renovar(ComandoRenovar comandoRenovar);

    record ComandoRenovar(String refreshToken){};
    record ResultadoRenovacion(String accessToken){};

}
