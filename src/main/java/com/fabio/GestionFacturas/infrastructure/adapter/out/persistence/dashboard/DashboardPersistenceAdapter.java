package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.dashboard;

import com.fabio.GestionFacturas.application.dashboard.port.out.DashboardConsultaPort;
import com.fabio.GestionFacturas.domain.gasto.Gasto;
import com.fabio.GestionFacturas.domain.ingreso.Ingreso;
import com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.gasto.GastoJpaRepository;
import com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.gasto.GastoMapper;
import com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.ingreso.IngresoJpaRepository;
import com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.ingreso.IngresoMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Implements {@link DashboardConsultaPort} by reusing the gasto/ingreso Spring Data repositories
 * and their mappers. Results are mapped to domain before returning, so JPA entities never escape.
 */
@Component
public class DashboardPersistenceAdapter implements DashboardConsultaPort {

    private final GastoJpaRepository gastoJpaRepository;
    private final IngresoJpaRepository ingresoJpaRepository;

    public DashboardPersistenceAdapter(GastoJpaRepository gastoJpaRepository,
                                       IngresoJpaRepository ingresoJpaRepository) {
        this.gastoJpaRepository = gastoJpaRepository;
        this.ingresoJpaRepository = ingresoJpaRepository;
    }

    @Override
    public List<Gasto> buscarGastosPorPeriodo(Long usuarioId, LocalDate desde, LocalDate hasta) {
        return gastoJpaRepository.findByUsuarioIdAndFechaEmisionBetween(usuarioId, desde, hasta)
                .stream()
                .map(GastoMapper::aDominio)
                .toList();
    }

    @Override
    public List<Ingreso> buscarIngresosPorPeriodo(Long usuarioId, LocalDate desde, LocalDate hasta) {
        return ingresoJpaRepository.findByUsuarioIdAndFechaEmisionBetween(usuarioId, desde, hasta)
                .stream()
                .map(IngresoMapper::aDominio)
                .toList();
    }
}
