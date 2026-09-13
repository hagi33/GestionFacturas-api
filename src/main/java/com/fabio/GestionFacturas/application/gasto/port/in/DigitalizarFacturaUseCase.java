package com.fabio.GestionFacturas.application.gasto.port.in;

import com.fabio.GestionFacturas.domain.gasto.Gasto;

public interface DigitalizarFacturaUseCase {

    Gasto digitalizar(ComandoDigitalizarFactura comando);

    record ComandoDigitalizarFactura(
            Long usuarioId,
            byte[] contenido,
            String nombreOriginal,
            String contentType
    ) {}

}
