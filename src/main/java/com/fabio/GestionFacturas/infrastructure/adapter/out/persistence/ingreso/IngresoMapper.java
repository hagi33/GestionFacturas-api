package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.ingreso;

import com.fabio.GestionFacturas.domain.ingreso.Ingreso;
import com.fabio.GestionFacturas.domain.shared.Dinero;

import java.math.BigDecimal;

/**
 * Converts between the domain {@link Ingreso} and {@link IngresoJpaEntity} — the third of the
 * three models (DTO / domain / JPA entity), used only by {@code IngresoPersistenceAdapter}.
 */
public class IngresoMapper {

    public IngresoMapper() {
    }

    public static IngresoJpaEntity aEntidad(Ingreso ingreso) {
        String moneda = extraerMoneda(ingreso);

        return new IngresoJpaEntity(
                ingreso.getId(),
                ingreso.getUsuarioId(),
                ingreso.getClienteId(),
                ingreso.getConcepto(),
                ingreso.getFechaEmision(),
                extraerCantidad(ingreso.getBaseImponible()),
                extraerCantidad(ingreso.getIva()),
                extraerCantidad(ingreso.getTotal()),
                moneda,
                ingreso.getEstadoCobro(),
                ingreso.getFechaCobro(),
                ingreso.getCreadoEn()
        );
    }

    public static Ingreso aDominio(IngresoJpaEntity entidad) {
        String moneda = entidad.getMoneda();

        return new Ingreso(
                entidad.getId(),
                entidad.getUsuarioId(),
                entidad.getClienteId(),
                entidad.getConcepto(),
                entidad.getFechaEmision(),
                construirDinero(entidad.getBaseImponible(), moneda),
                construirDinero(entidad.getIva(), moneda),
                construirDinero(entidad.getTotal(), moneda),
                entidad.getEstadoCobro(),
                entidad.getFechaCobro(),
                entidad.getCreadoEn()
        );
    }

    private static BigDecimal extraerCantidad(Dinero dinero) {
        if (dinero == null) {
            return null;
        }
        return dinero.cantidad();
    }

    // Same fallback the web mapper uses: domain has one Dinero per amount, entity has a single moneda column
    private static String extraerMoneda(Ingreso ingreso) {
        Dinero total = ingreso.getTotal();
        if (total != null) {
            return total.moneda();
        }
        Dinero base = ingreso.getBaseImponible();
        if (base != null) {
            return base.moneda();
        }
        return "EUR";
    }

    private static Dinero construirDinero(BigDecimal cantidad, String moneda) {
        if (cantidad == null) {
            return null;
        }
        return new Dinero(cantidad, moneda);
    }
}
