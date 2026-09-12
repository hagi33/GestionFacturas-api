package com.fabio.GestionFacturas.application.usuario.port.in;

public interface LogoutUseCase {

    void logout(ComandoLogout comandoLogout);

    record ComandoLogout(String refreshToken){};

}
