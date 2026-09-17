package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.gasto;

import com.fabio.GestionFacturas.domain.gasto.EstadoGasto;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Persistence model for Gasto — a plain JPA entity, separate from the domain {@link
 * com.fabio.GestionFacturas.domain.gasto.Gasto}. {@code GastoMapper} converts between the two;
 * this class never leaves the persistence adapter. {@code @Enumerated(STRING)} stores
 * {@code estado} as readable text in the DB instead of an ordinal, so column values survive
 * enum reordering.
 */
@Entity
@Table(name = "gasto")
public class GastoJpaEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "categoria_id")
    private Long categoriaId;

    private String emisor;

    @Column(name = "fecha_emision")
    private LocalDate fechaEmision;

    @Column(name = "base_imponible")
    private BigDecimal baseImponible;

    private BigDecimal iva;

    private BigDecimal total;

    @Column(name = "referencia_archivo")
    private String referenciaArchivo;

    // One shared currency column for all three Dinero amounts (base/iva/total) — see GastoMapper
    @Column(nullable = false)
    private String moneda;

    @Column(nullable = false)
    private boolean deducible;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoGasto estado;

    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn;

    /** No-args constructor required by JPA/Hibernate to instantiate entities via reflection. */
    public GastoJpaEntity() {


    }

    public GastoJpaEntity(Long id, Long usuarioId, Long categoriaId, String emisor, LocalDate fechaEmision,
                            BigDecimal baseImponible, BigDecimal iva, BigDecimal total, String referenciaArchivo,
                            String moneda, boolean deducible, EstadoGasto estado, LocalDateTime creadoEn) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.categoriaId = categoriaId;
        this.emisor = emisor;
        this.fechaEmision = fechaEmision;
        this.baseImponible = baseImponible;
        this.iva = iva;
        this.total = total;
        this.referenciaArchivo = referenciaArchivo;
        this.moneda = moneda;
        this.deducible = deducible;
        this.estado = estado;
        this.creadoEn = creadoEn;
    }

    public Long getId() {
        return id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public String getEmisor() {
        return emisor;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public BigDecimal getBaseImponible() {
        return baseImponible;
    }

    public BigDecimal getIva() {
        return iva;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public String getReferenciaArchivo() {
        return referenciaArchivo;
    }

    public String getMoneda() {
        return moneda;
    }

    public boolean isDeducible() {
        return deducible;
    }

    public EstadoGasto getEstado() {
        return estado;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }



}
