package com.fabio.GestionFacturas.domain.dashboard;

import com.fabio.GestionFacturas.domain.ingreso.Ingreso;
import com.fabio.GestionFacturas.domain.shared.Dinero;
import java.math.BigDecimal;
import java.util.List;

/**
 * Pure-domain calculator: builds PendientesCobro from ingresos that the caller has already
 * filtered to PENDIENTE. No Spring/JPA on purpose (hexagonal architecture).
 */
public class CalculadoraPendientesCobro {

    public PendientesCobro calcular(List<Ingreso> pendientes) {
        BigDecimal total = BigDecimal.ZERO;

        for (Ingreso ingreso : pendientes) {
            total = total.add(cantidadOCero(ingreso.getTotal()));
        }

        return new PendientesCobro(List.copyOf(pendientes), Dinero.deEuros(total));
    }

    /** Amounts can be null in the domain; they count as zero in the sum. */
    private BigDecimal cantidadOCero(Dinero dinero) {
        if (dinero == null) {
            return BigDecimal.ZERO;
        }
        return dinero.cantidad();
    }
}
