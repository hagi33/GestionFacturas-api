package com.fabio.GestionFacturas.application.usuario.port.in;

import com.fabio.GestionFacturas.domain.usuario.Usuario;
import com.fabio.GestionFacturas.domain.usuario.Usuario;


/** Inbound port for sign-up. Implemented by {@code RegistrarUsuarioService}, called by {@code AuthController}. */
public interface RegistrarUsuarioUseCase {


    Usuario registrar (ComandoRegistrar comando);

    /** Raw password travels in this command only as far as the service, which hashes it before storing. */
    record ComandoRegistrar(String email, String nombre, String password){}




}
