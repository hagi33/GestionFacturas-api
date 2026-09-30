package com.fabio.GestionFacturas.application.dashboard.service;

import com.fabio.GestionFacturas.application.dashboard.port.in.ObtenerRentabilidadClientesUseCase;
import com.fabio.GestionFacturas.application.dashboard.port.out.DashboardConsultaPort;
import com.fabio.GestionFacturas.domain.dashboard.CalculadoraRentabilidadClientes;
import com.fabio.GestionFacturas.domain.dashboard.RentabilidadCliente;
import com.fabio.GestionFacturas.domain.gasto.Gasto;
import com.fabio.GestionFacturas.domain.ingreso.Ingreso;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implements {@link ObtenerRentabilidadClientesUseCase}: thin orchestration — loads the user's gastos and
 * ingresos for the period and delegates the per-client figures to the domain {@link CalculadoraRentabilidadClientes}.
 */
@Service
public class RentabilidadClientesService implements ObtenerRentabilidadClientesUseCase {

    private final DashboardConsultaPort dashboardConsulta;
    private final CalculadoraRentabilidadClientes calculadora;

    public RentabilidadClientesService(DashboardConsultaPort dashboardConsulta,
                                       CalculadoraRentabilidadClientes calculadora) {
        this.dashboardConsulta = dashboardConsulta;
        this.calculadora = calculadora;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentabilidadCliente> obtenerRentabilidad(ComandoObtenerRentabilidad comando) {
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
