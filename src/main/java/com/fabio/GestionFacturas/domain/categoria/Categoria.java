package com.fabio.GestionFacturas.domain.categoria;

import java.util.Objects;

public class Categoria {

    private final Long id;
    private final String nombre;
    private final boolean deduciblePorDefecto;

    public Categoria(Long id, String nombre, boolean deduciblePorDefecto){
        this.id = id;
        this.nombre = Objects.requireNonNull(nombre, "El nombre no puede ser nulo");
        this.deduciblePorDefecto = deduciblePorDefecto;

    }

    public Long getId() {return id;}
    public String getNombre() {return nombre;}
    public boolean isDeduciblePorDefecto() {return deduciblePorDefecto;}



}
