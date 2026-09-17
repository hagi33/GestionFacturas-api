package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/** Persistence model for Usuario — mapped to/from the domain object by {@code UsuarioMapper}. */
@Entity
@Table(name = "usuario")
public class UsuarioJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    // Column named "password" but always holds the BCrypt hash — the raw password never reaches this class
    @Column(name = "password", nullable = false)
    private String password;

    @Column(nullable = false)
    private String nombre;

    // insertable/updatable = false: set once by a DB default, never written from the Java side
    @Column(name = "creado_en", insertable = false, updatable = false)
    private LocalDateTime creadoEn;

    /** No-args constructor required by JPA/Hibernate. */
    protected UsuarioJpaEntity() {
    }

    public UsuarioJpaEntity(Long id, String email, String password, String nombre) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getNombre() {
        return nombre;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }
}
