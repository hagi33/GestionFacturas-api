package com.fabio.GestionFacturas.application.dashboard.service;

import com.fabio.GestionFacturas.application.dashboard.port.in.ObtenerResumenPeriodoUseCase;
import com.fabio.GestionFacturas.application.dashboard.port.out.DashboardConsultaPort;
import com.fabio.GestionFacturas.domain.dashboard.CalculadoraResumenPeriodo;
import com.fabio.GestionFacturas.domain.dashboard.ResumenPeriodo;
import com.fabio.GestionFacturas.domain.gasto.Gasto;
import com.fabio.GestionFacturas.domain.ingreso.Ingreso;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implements {@link ObtenerResumenPeriodoUseCase}: thin orchestration — loads the user's gastos and
 * ingresos for the period and delegates every figure to the domain {@link CalculadoraResumenPeriodo}.
 */
@Service
public class ResumenPeriodoService implements ObtenerResumenPeriodoUseCase {

    private final DashboardConsultaPort dashboardConsulta;
    private final CalculadoraResumenPeriodo calculadora;

    public ResumenPeriodoService(DashboardConsultaPort dashboardConsulta, CalculadoraResumenPeriodo calculadora) {
        this.dashboardConsulta = dashboardConsulta;
        this.calculadora = calculadora;
    }

    @Override
    @Transactional(readOnly = true)
    public ResumenPeriodo obtenerResumen(ComandoObtenerResumen comando) {
        if (comando.desde().isAfter(comando.hasta())) {
            throw new IllegalArgumentException("La fecha 'desde' no puede ser posterior a 'hasta'");
        }

        List<Gasto> gastos = dashboardConsulta.buscarGastosPorPeriodo(
                comando.usuarioId(), comando.desde(), comando.hasta());
        List<Ingreso> ingresos = dashboardConsulta.buscarIngresosPorPeriodo(
                comando.usuarioId(), comando.desde(), comando.hasta());

        return calculadora.calcular(gastos, ingresos);
    }
}
