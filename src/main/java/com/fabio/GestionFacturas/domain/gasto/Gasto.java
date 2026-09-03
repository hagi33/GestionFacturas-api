package com.fabio.GestionFacturas.domain.gasto;


import com.fabio.GestionFacturas.domain.shared.Dinero;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Gasto {

    private final Long id;
    private final Long usuarioId;
    private Long categoriaId;
    private String emisor;
    private LocalDate fechaEmision;
    private Dinero baseImponible;
    private Dinero iva;
    private Dinero total;
    private boolean deducible;
    private EstadoGasto estado;
    private final LocalDateTime creadoEn;

    public Gasto(Long id, Long usuarioId, Long categoriaId, String emisor,
                   LocalDate fechaEmision, Dinero baseImponible, Dinero iva, Dinero total,
                   boolean deducible, EstadoGasto estado, LocalDateTime creadoEn) {
        if (usuarioId == null) {
            throw new GastoInvalidoException("El gasto debe tener un usuario");
        }

        EstadoGasto estadoInicial = estado;
        if (estadoInicial == null) {
            estadoInicial = EstadoGasto.BORRADOR;
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

    public static Gasto crearBorrador(Long usuarioId, String emisor, LocalDate fechaEmision,
                                        Dinero baseImponible, Dinero iva, Dinero total) {
        return new Gasto(null, usuarioId, null, emisor, fechaEmision,
                baseImponible, iva, total, false, EstadoGasto.BORRADOR, LocalDateTime.now());
    }

    public void revisar(Long categoriaId, boolean deducible) {
        this.categoriaId = categoriaId;
        this.deducible = deducible;
        this.estado = EstadoGasto.REVISADA;
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
    public EstadoGasto getEstado() { return estado; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
}
