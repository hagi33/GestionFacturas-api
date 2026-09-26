package com.fabio.GestionFacturas.domain.dashboard;

import com.fabio.GestionFacturas.domain.gasto.Gasto;
import com.fabio.GestionFacturas.domain.ingreso.EstadoCobro;
import com.fabio.GestionFacturas.domain.ingreso.Ingreso;
import com.fabio.GestionFacturas.domain.shared.Dinero;
import java.math.BigDecimal;
import java.util.List;

/**
 * Pure-domain calculator: builds a ResumenPeriodo from gastos and ingresos that the caller
 * has already filtered to the period. No Spring/JPA on purpose (hexagonal architecture).
 */
public class CalculadoraResumenPeriodo {

    public ResumenPeriodo calcular(List<Gasto> gastos, List<Ingreso> ingresos) {
        BigDecimal facturadoTotal = BigDecimal.ZERO;
        BigDecimal facturadoBase = BigDecimal.ZERO;
        BigDecimal cobradoTotal = BigDecimal.ZERO;
        BigDecimal cobradoBase = BigDecimal.ZERO;

        for (Ingreso ingreso : ingresos) {
            BigDecimal total = cantidadOCero(ingreso.getTotal());
            BigDecimal base = cantidadOCero(ingreso.getBaseImponible());
            facturadoTotal = facturadoTotal.add(total);
            facturadoBase = facturadoBase.add(base);
            if (ingreso.getEstadoCobro() == EstadoCobro.COBRADA) {
                cobradoTotal = cobradoTotal.add(total);
                cobradoBase = cobradoBase.add(base);
            }
        }

        BigDecimal gastosTotal = BigDecimal.ZERO;
        BigDecimal gastosBase = BigDecimal.ZERO;

        for (Gasto gasto : gastos) {
            gastosTotal = gastosTotal.add(cantidadOCero(gasto.getTotal()));
            gastosBase = gastosBase.add(cantidadOCero(gasto.getBaseImponible()));
        }

        return new ResumenPeriodo(
                Dinero.deEuros(facturadoTotal),
                Dinero.deEuros(facturadoBase),
                Dinero.deEuros(cobradoTotal),
                Dinero.deEuros(cobradoBase),
                Dinero.deEuros(gastosTotal),
                Dinero.deEuros(gastosBase),
                Dinero.deEuros(cobradoTotal.subtract(gastosTotal)),
                Dinero.deEuros(cobradoBase.subtract(gastosBase)),
                Dinero.deEuros(facturadoTotal.subtract(gastosTotal)),
                Dinero.deEuros(facturadoBase.subtract(gastosBase)));
    }

    /** Amounts can be null in the domain (e.g. OCR drafts); they count as zero in the sums. */
    private BigDecimal cantidadOCero(Dinero dinero) {
        if (dinero == null) {
            return BigDecimal.ZERO;
        }
        return dinero.cantidad();
    }
}
