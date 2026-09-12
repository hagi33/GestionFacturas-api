package com.fabio.GestionFacturas.application.usuario.port.out;

import java.time.LocalDateTime;

public interface RefreshTokenGeneradorPort {

    String generar();

    String hashear(String tokenPlano);

    LocalDateTime calcularExpiracion();

}
