package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Persistence model for RefreshToken. {@code tokenHash} is unique so a lookup by hash
 * (see {@code RefreshTokenJpaRepository}) is a direct indexed match, not a scan.
 */
@Entity
@Table(name = "refresh_token")
public class RefreshTokenJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "expira_en", nullable = false)
    private LocalDateTime expiraEn;

    @Column(nullable = false)
    private boolean revocado;

    @Column(name = "creado_en", insertable = false, updatable = false)
    private LocalDateTime creadoEn;

    /** No-args constructor required by JPA/Hibernate. */
    protected RefreshTokenJpaEntity() {
    }

    public RefreshTokenJpaEntity(Long id, Long usuarioId, String tokenHash, LocalDateTime expiraEn, boolean revocado) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.tokenHash = tokenHash;
        this.expiraEn = expiraEn;
        this.revocado = revocado;
    }

    public Long getId() {
        return id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public LocalDateTime getExpiraEn() {
        return expiraEn;
    }

    public boolean isRevocado() {
        return revocado;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }
}
