package com.fabio.GestionFacturas.application.usuario.port.in;

/** Inbound port for logout. Implemented by {@code LogoutService}, which revokes (not deletes) the refresh token. */
public interface LogoutUseCase {

    void logout(ComandoLogout comandoLogout);

    record ComandoLogout(String refreshToken){};

}
