package com.fabio.GestionFacturas.application.usuario.port.out;

public interface TokenGeneradorPort {

    String generarAccessToken(Long usuarioId, String email);

}
