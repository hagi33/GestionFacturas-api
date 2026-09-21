package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.gasto;

import com.fabio.GestionFacturas.domain.gasto.Gasto;
import com.fabio.GestionFacturas.domain.shared.Dinero;

import java.math.BigDecimal;

/**
 * Converts between the domain {@link Gasto} and {@link GastoJpaEntity} — the third of the three
 * models (DTO / domain / JPA entity), used only by {@code GastoPersistenceAdapter}.
 */
public class GastoMapper {


    public GastoMapper() {
    }


    public static GastoJpaEntity aEntidad(Gasto gasto){

        String moneda = extraerMoneda(gasto);

        return new GastoJpaEntity(
                gasto.getId(),
                gasto.getUsuarioId(),
                gasto.getCategoriaId(),
                gasto.getClienteId(),
                gasto.getEmisor(),
                gasto.getFechaEmision(),
                extraerCantidad(gasto.getBaseImponible()),
                extraerCantidad(gasto.getIva()),
                extraerCantidad(gasto.getTotal()),
                gasto.getReferenciaArchivo(),
                moneda,
                gasto.isDeducible(),
                gasto.getEstado(),
                gasto.getCreadoEn()
                );
    }

    public static Gasto aDominio(GastoJpaEntity entidad){
        String moneda = entidad.getMoneda();

        return new Gasto(
                entidad.getId(),
                entidad.getUsuarioId(),
                entidad.getCategoriaId(),
                entidad.getClienteId(),
                entidad.getEmisor(),
                entidad.getFechaEmision(),
                construirDinero(entidad.getBaseImponible(), moneda),
                construirDinero(entidad.getIva(), moneda),
                construirDinero(entidad.getTotal(), moneda),
                entidad.getReferenciaArchivo(),
                entidad.isDeducible(),
                entidad.getEstado(),
                entidad.getCreadoEn()
        );

    }

    private static BigDecimal extraerCantidad(Dinero dinero){
        if (dinero == null) {
            return null;
        }
        return dinero.cantidad();
    }




    // Same fallback the web mapper uses: domain has one Dinero per amount, entity has a single moneda column
    private static String extraerMoneda(Gasto gasto){
        Dinero total = gasto.getTotal();

        if (total != null){
            return total.moneda();
        }
        Dinero base = gasto.getBaseImponible();
        if (base != null){
            return base.moneda();
        }
        return "EUR";
    }


    private static Dinero construirDinero(BigDecimal cantidad, String moneda){
        if (cantidad == null){
            return null;
        }
        return new Dinero(cantidad, moneda);

    }

}
