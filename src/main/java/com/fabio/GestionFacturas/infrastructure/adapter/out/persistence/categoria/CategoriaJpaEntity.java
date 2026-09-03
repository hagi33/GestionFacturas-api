package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.categoria;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "categoria")
public class CategoriaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "deducible_defecto", nullable = false)
    private boolean deduciblePorDefecto;

    protected CategoriaJpaEntity() {
    }

    public CategoriaJpaEntity(Long id, String nombre, boolean deduciblePorDefecto) {
        this.id = id;
        this.nombre = nombre;
        this.deduciblePorDefecto = deduciblePorDefecto;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isDeduciblePorDefecto() {
        return deduciblePorDefecto;
    }
}
