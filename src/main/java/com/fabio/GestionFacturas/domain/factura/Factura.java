package com.fabio.GestionFacturas.domain.factura;


import com.fabio.GestionFacturas.domain.shared.Dinero;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Factura {

    private final Long id;
    private final Long usuarioId;
    private Long categoriaId;
    private String emisor;
    private LocalDate fechaEmision;
    private Dinero baseImponible;
    private Dinero iva;
    private Dinero total;
    private boolean deducible;
    private EstadoFactura estado;
    private final LocalDateTime creadoEn;

    public Factura(Long id, Long usuarioId, Long categoriaId, String emisor,
                   LocalDate fechaEmision, Dinero baseImponible, Dinero iva, Dinero total,
                   boolean deducible, EstadoFactura estado, LocalDateTime creadoEn) {
        if (usuarioId == null) {
            throw new FacturaInvalidaException("La factura debe tener un usuario");
        }

        EstadoFactura estadoInicial = estado;
        if (estadoInicial == null) {
            estadoInicial = EstadoFactura.BORRADOR;
        }

        LocalDateTime fechaCreacion = creadoEn;
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }

        this.id = id;
        this.usuarioId = usuarioId;
        this.categoriaId = categoriaId;
        this.emisor = emisor;
        this.fechaEmision = fechaEmision;
        this.baseImponible = baseImponible;
        this.iva = iva;
        this.total = total;
        this.deducible = deducible;
        this.estado = estadoInicial;
        this.creadoEn = fechaCreacion;
    }

    public static Factura crearBorrador(Long usuarioId, String emisor, LocalDate fechaEmision,
                                        Dinero baseImponible, Dinero iva, Dinero total) {
        return new Factura(null, usuarioId, null, emisor, fechaEmision,
                baseImponible, iva, total, false, EstadoFactura.BORRADOR, LocalDateTime.now());
    }

    public void revisar(Long categoriaId, boolean deducible) {
        this.categoriaId = categoriaId;
        this.deducible = deducible;
        this.estado = EstadoFactura.REVISADA;
    }

    public Long getId() { return id; }
    public Long getUsuarioId() { return usuarioId; }
    public Long getCategoriaId() { return categoriaId; }
    public String getEmisor() { return emisor; }
    public LocalDate getFechaEmision() { return fechaEmision; }
    public Dinero getBaseImponible() { return baseImponible; }
    public Dinero getIva() { return iva; }
    public Dinero getTotal() { return total; }
    public boolean isDeducible() { return deducible; }
    public EstadoFactura getEstado() { return estado; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
}