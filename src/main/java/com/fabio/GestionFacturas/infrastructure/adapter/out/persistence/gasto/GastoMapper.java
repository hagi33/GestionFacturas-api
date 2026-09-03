package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.gasto;

import com.fabio.GestionFacturas.domain.gasto.Gasto;
import com.fabio.GestionFacturas.domain.shared.Dinero;

import java.math.BigDecimal;

public class GastoMapper {


    public GastoMapper() {
    }


    public static GastoJpaEntity aEntidad(Gasto gasto){

        String moneda = extraerMoneda(gasto);

        return new GastoJpaEntity(
                gasto.getId(),
                gasto.getUsuarioId(),
                gasto.getCategoriaId(),
                gasto.getEmisor(),
                gasto.getFechaEmision(),
                extraerCantidad(gasto.getBaseImponible()),
                extraerCantidad(gasto.getIva()),
                extraerCantidad(gasto.getTotal()),
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
                entidad.getEmisor(),
                entidad.getFechaEmision(),
                construirDinero(entidad.getBaseImponible(), moneda),
                construirDinero(entidad.getIva(), moneda),
                construirDinero(entidad.getTotal(), moneda),
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
