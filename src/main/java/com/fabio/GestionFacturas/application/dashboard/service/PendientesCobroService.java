package com.fabio.GestionFacturas.application.dashboard.service;

import com.fabio.GestionFacturas.application.dashboard.port.in.ObtenerPendientesCobroUseCase;
import com.fabio.GestionFacturas.application.dashboard.port.out.DashboardConsultaPort;
import com.fabio.GestionFacturas.domain.dashboard.CalculadoraPendientesCobro;
import com.fabio.GestionFacturas.domain.dashboard.PendientesCobro;
import com.fabio.GestionFacturas.domain.ingreso.Ingreso;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implements {@link ObtenerPendientesCobroUseCase}: thin orchestration — loads the user's PENDIENTE
 * ingresos and delegates the total to the domain {@link CalculadoraPendientesCobro}.
 */
@Service
public class PendientesCobroService implements ObtenerPendientesCobroUseCase {

    private final DashboardConsultaPort dashboardConsulta;
    private final CalculadoraPendientesCobro calculadora;

    public PendientesCobroService(DashboardConsultaPort dashboardConsulta, CalculadoraPendientesCobro calculadora) {
        this.dashboardConsulta = dashboardConsulta;
        this.calculadora = calculadora;
    }

    @Override
    @Transactional(readOnly = true)
    public PendientesCobro obtenerPendientes(ComandoObtenerPendientesCobro comando) {
        List<Ingreso> pendientes = dashboardConsulta.buscarIngresosPendientesDeCobro(comando.usuarioId());
        return calculadora.calcular(pendientes);
    }
}
