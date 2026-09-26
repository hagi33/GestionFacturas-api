package com.fabio.GestionFacturas.domain.dashboard;

import com.fabio.GestionFacturas.domain.shared.Dinero;

/**
 * Period summary for the dashboard. Every figure comes as total (with IVA) and base (without IVA).
 * Never null: an absent amount is zero.
 *
 * @param facturadoTotal          sum of total of ALL ingresos (regardless of estadoCobro)
 * @param facturadoBase           sum of baseImponible of ALL ingresos
 * @param cobradoTotal            sum of total of COBRADA ingresos only
 * @param cobradoBase             sum of baseImponible of COBRADA ingresos only
 * @param gastosTotal             sum of total of all gastos
 * @param gastosBase              sum of baseImponible of all gastos
 * @param beneficioCajaTotal      cobradoTotal - gastosTotal
 * @param beneficioCajaBase       cobradoBase - gastosBase
 * @param beneficioFacturadoTotal facturadoTotal - gastosTotal
 * @param beneficioFacturadoBase  facturadoBase - gastosBase
 */
public record ResumenPeriodo(
        Dinero facturadoTotal,
        Dinero facturadoBase,
        Dinero cobradoTotal,
        Dinero cobradoBase,
        Dinero gastosTotal,
        Dinero gastosBase,
        Dinero beneficioCajaTotal,
        Dinero beneficioCajaBase,
        Dinero beneficioFacturadoTotal,
        Dinero beneficioFacturadoBase) {
}
