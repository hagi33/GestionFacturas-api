package com.fabio.GestionFacturas.domain.usuario;

import java.util.Objects;

public class Usuario {

    private final Long id;
    private final String email;
    private final String nombre;
    private final String passwordHash;

    public Usuario(Long id, String email, String nombre, String passwordHash){
        this.id = id;
        this.email = Objects.requireNonNull(email, "El email no puede ser nulo");
        this.nombre = Objects.requireNonNull(nombre, "EL nombre no puede ser nulo");
        this.passwordHash = passwordHash;
    }

    public Long getId() {return id;}

    public String getEmail() {return email;}

    public String getNombre() {return nombre;}

    public String getPasswordHash() {return passwordHash;}

}

