package com.fabio.GestionFacturas.domain.cliente;

/**
 * Core domain entity: a client belonging to a user, kept for organization (never billed
 * directly by this app). Plain POJO on purpose (hexagonal architecture) — no JPA/Spring
 * annotations here; persistence mapping lives in the infrastructure layer's JpaEntity + Mapper.
 */
public class Cliente {

    private final Long id;
    private final Long usuarioId;
    private String nombre;
    private final String nif;
    private String email;
    private String telefono;
    private boolean activo;

    /**
     * Full constructor used by mappers to rebuild a Cliente from persistence.
     * Enforces the domain's own invariants (self-validation), independent of any framework.
     */
    public Cliente(Long id, Long usuarioId, String nombre, String nif, String email,
                    String telefono, boolean activo) {
        if (usuarioId == null) {
            throw new ClienteInvalidoException("El cliente debe tener un usuario");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new ClienteInvalidoException("El cliente debe tener un nombre");
        }
        if (nif == null || nif.isBlank()) {
            throw new ClienteInvalidoException("El cliente debe tener un NIF");
        }

        this.id = id;
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.nif = nif;
        this.email = email;
        this.telefono = telefono;
        this.activo = activo;
    }

    /** Factory for a newly registered client: starts life active. */
    public static Cliente crearActivo(Long usuarioId, String nombre, String nif, String email, String telefono) {
        return new Cliente(null, usuarioId, nombre, nif, email, telefono, true);
    }

    /** Soft delete: flips the client to inactive without removing the row (keeps history for past gastos). */
    public void desactivar() {
        this.activo = false;
    }

    /** Updates the mutable fields (nombre, email, telefono). nif and usuarioId never change after creation. */
    public void actualizarDatos(String nombre, String email, String telefono) {
        if (nombre == null || nombre.isBlank()) {
            throw new ClienteInvalidoException("El cliente debe tener un nombre");
        }

        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
    }

    public Long getId() { return id; }
    public Long getUsuarioId() { return usuarioId; }
    public String getNombre() { return nombre; }
    public String getNif() { return nif; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public boolean isActivo() { return activo; }
}
