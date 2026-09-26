package com.fabio.GestionFacturas.infrastructure.adapter.in.web.dashboard;

import com.fabio.GestionFacturas.domain.dashboard.ResumenPeriodo;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.dashboard.dto.ResumenPeriodoResponse;

/** Converts the domain {@link ResumenPeriodo} into its web DTO, so domain objects never cross the HTTP boundary. */
public class DashboardWebMapper {

    private DashboardWebMapper() {
    }

    public static ResumenPeriodoResponse aRespuesta(ResumenPeriodo resumen) {
        return new ResumenPeriodoResponse(
                resumen.facturadoTotal().cantidad(),
                resumen.facturadoBase().cantidad(),
                resumen.cobradoTotal().cantidad(),
                resumen.cobradoBase().cantidad(),
                resumen.gastosTotal().cantidad(),
                resumen.gastosBase().cantidad(),
                resumen.beneficioCajaTotal().cantidad(),
                resumen.beneficioCajaBase().cantidad(),
                resumen.beneficioFacturadoTotal().cantidad(),
                resumen.beneficioFacturadoBase().cantidad());
    }
}
