package com.fabio.GestionFacturas.infrastructure.adapter.in.web.dashboard;

import com.fabio.GestionFacturas.application.dashboard.port.in.ObtenerResumenPeriodoUseCase;
import com.fabio.GestionFacturas.application.dashboard.port.in.ObtenerResumenPeriodoUseCase.ComandoObtenerResumen;
import com.fabio.GestionFacturas.domain.dashboard.ResumenPeriodo;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.dashboard.dto.ResumenPeriodoResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * Web adapter (inbound) for the dashboard. Depends only on the inbound use-case port;
 * {@code usuarioId} always comes from {@code @AuthenticationPrincipal}, so a user only sees their own data.
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final ObtenerResumenPeriodoUseCase obtenerResumenPeriodoUseCase;

    public DashboardController(ObtenerResumenPeriodoUseCase obtenerResumenPeriodoUseCase) {
        this.obtenerResumenPeriodoUseCase = obtenerResumenPeriodoUseCase;
    }

    @GetMapping("/resumen")
    public ResumenPeriodoResponse resumen(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @AuthenticationPrincipal Long usuarioId) {
        ResumenPeriodo resumen = obtenerResumenPeriodoUseCase.obtenerResumen(
                new ComandoObtenerResumen(usuarioId, desde, hasta));
        return DashboardWebMapper.aRespuesta(resumen);
    }
}
