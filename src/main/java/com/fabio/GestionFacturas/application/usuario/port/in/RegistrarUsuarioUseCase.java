package com.fabio.GestionFacturas.application.usuario.port.in;

import com.fabio.GestionFacturas.domain.usuario.Usuario;
import com.fabio.GestionFacturas.domain.usuario.Usuario;


public interface RegistrarUsuarioUseCase {


    Usuario registrar (ComandoRegistrar comando);

    record ComandoRegistrar(String email, String nombre, String password){}




}
