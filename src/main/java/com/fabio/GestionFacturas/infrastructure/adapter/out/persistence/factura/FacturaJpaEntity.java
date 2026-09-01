package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.factura;

import com.fabio.GestionFacturas.domain.factura.EstadoFactura;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "factura")
public class FacturaJpaEntity {


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

    @Column(nullable = false)
    private String moneda;

    @Column(nullable = false)
    private boolean deducible;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoFactura estado;

    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn;

    public FacturaJpaEntity() {


    }

    public FacturaJpaEntity(Long id, Long usuarioId, Long categoriaId, String emisor, LocalDate fechaEmision,
                            BigDecimal baseImponible, BigDecimal iva, BigDecimal total,
                            String moneda, boolean deducible, EstadoFactura estado, LocalDateTime creadoEn) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.categoriaId = categoriaId;
        this.emisor = emisor;
        this.fechaEmision = fechaEmision;
        this.baseImponible = baseImponible;
        this.iva = iva;
        this.total = total;
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

    public String getMoneda() {
        return moneda;
    }

    public boolean isDeducible() {
        return deducible;
    }

    public EstadoFactura getEstado() {
        return estado;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }



}
