package com.fabio.GestionFacturas.domain.usuario;

import java.time.LocalDateTime;

/**
 * Domain record of a refresh token. Only the hash is stored here (and in persistence) —
 * the raw token is never kept, so a DB leak alone can't be used to forge sessions.
 * Revocation is a flag (not a delete) so logout history/audit is preserved.
 */
public class RefreshToken {


    private final Long id;
    private final Long usuarioId;
    private final String tokenHash;
    private final LocalDateTime expiraEn;
    private boolean revocado;
    private final LocalDateTime creadoEn;

    public RefreshToken(boolean revocado, Long id, Long usuarioId, String tokenHash, LocalDateTime expiraEn, LocalDateTime creadoEn) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El refresh token debe tener un usuario");
        }
        if (tokenHash == null) {
            throw new IllegalArgumentException("El refresh token debe tener un hash");
        }
        this.revocado = revocado;
        this.id = id;
        this.usuarioId = usuarioId;
        this.tokenHash = tokenHash;
        this.expiraEn = expiraEn;

        LocalDateTime fechaCreacion = creadoEn;
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        this.creadoEn = fechaCreacion;
    }

    /** Valid only if neither revoked nor expired — checked on every refresh/logout attempt. */
    public boolean esValido() {
        if (revocado) {
            return false;
        }
        if (expiraEn.isBefore(LocalDateTime.now())) {
            return false;
        }
        return true;
    }

    /** Marks the token unusable without deleting the row (see class note). */
    public void revocar() {
        this.revocado = true;
    }

    public Long getId() { return id; }
    public Long getUsuarioId() { return usuarioId; }
    public String getTokenHash() { return tokenHash; }
    public LocalDateTime getExpiraEn() { return expiraEn; }
    public boolean isRevocado() { return revocado; }
    public LocalDateTime getCreadoEn() { return creadoEn; }

}
