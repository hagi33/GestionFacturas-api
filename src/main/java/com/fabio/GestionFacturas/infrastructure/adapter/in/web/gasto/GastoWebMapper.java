package com.fabio.GestionFacturas.infrastructure.adapter.in.web.gasto;

import com.fabio.GestionFacturas.application.gasto.port.in.CrearGastoUseCase.ComandoCrearGasto;
import com.fabio.GestionFacturas.domain.gasto.Gasto;
import com.fabio.GestionFacturas.domain.shared.Dinero;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.gasto.dto.CrearGastoRequest;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.gasto.dto.GastoResponse;

/**
 * Converts between web DTOs and the domain {@link Gasto} — the boundary that keeps the
 * three models (DTO / domain / JPA entity) separate. Static/stateless: no port dependency, pure mapping.
 */
public class GastoWebMapper {

    public GastoWebMapper() {
    }

    public static ComandoCrearGasto aComando(CrearGastoRequest request, Long usuarioId) {
        return new ComandoCrearGasto(
                usuarioId,
                request.emisor(),
                request.fechaEmision(),
                request.baseImponible(),
                request.iva(),
                request.total(),
                request.moneda(),
                request.clienteId()
        );
    }

    public static GastoResponse aRespuesta(Gasto gasto) {
        String moneda = extraerMoneda(gasto);

        return new GastoResponse(
                gasto.getId(),
                gasto.getUsuarioId(),
                gasto.getCategoriaId(),
                gasto.getClienteId(),
                gasto.getEmisor(),
                gasto.getFechaEmision(),
                extraerCantidad(gasto.getBaseImponible()),
                extraerCantidad(gasto.getIva()),
                moneda,
                gasto.isDeducible(),
                gasto.getEstado().name(),
                gasto.getCreadoEn()
        );
    }

    private static java.math.BigDecimal extraerCantidad(Dinero dinero) {
        if (dinero == null) {
            return null;
        }
        return dinero.cantidad();
    }

    // Response has one top-level "moneda" field even though each Dinero carries its own currency;
    // falls back through total -> base -> EUR since a fresh BORRADOR may have neither amount yet.
    private static String extraerMoneda(Gasto gasto) {
        Dinero total = gasto.getTotal();
        if (total != null) {
            return total.moneda();
        }
        Dinero base = gasto.getBaseImponible();
        if (base != null) {
            return base.moneda();
        }
        return "EUR";
    }
}
