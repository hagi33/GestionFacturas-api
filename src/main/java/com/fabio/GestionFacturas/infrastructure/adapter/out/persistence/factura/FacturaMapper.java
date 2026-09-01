package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.factura;

import com.fabio.GestionFacturas.domain.factura.Factura;
import com.fabio.GestionFacturas.domain.shared.Dinero;

import java.math.BigDecimal;

public class FacturaMapper {


    public FacturaMapper() {
    }


    public static FacturaJpaEntity aEntidad(Factura factura){

        String moneda = extraerMoneda(factura);

        return new FacturaJpaEntity(
                factura.getId(),
                factura.getUsuarioId(),
                factura.getCategoriaId(),
                factura.getEmisor(),
                factura.getFechaEmision(),
                extraerCantidad(factura.getBaseImponible()),
                extraerCantidad(factura.getIva()),
                extraerCantidad(factura.getTotal()),
                moneda,
                factura.isDeducible(),
                factura.getEstado(),
                factura.getCreadoEn()
                );
    }

    public static Factura aDominio(FacturaJpaEntity entidad){
        String moneda = entidad.getMoneda();

        return new Factura(
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




    private static String extraerMoneda(Factura factura){
        Dinero total = factura.getTotal();

        if (total != null){
            return total.moneda();
        }
        Dinero base = factura.getBaseImponible();
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
