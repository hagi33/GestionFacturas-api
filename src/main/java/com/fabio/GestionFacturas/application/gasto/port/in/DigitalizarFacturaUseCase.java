package com.fabio.GestionFacturas.application.gasto.port.in;

import com.fabio.GestionFacturas.domain.gasto.Gasto;

/**
 * Inbound port for the OCR flow: upload an invoice image, get back a BORRADOR Gasto.
 * Implemented by {@code DigitalizarFacturaService}, which internally fans out to three
 * outbound ports in sequence — {@code FileStoragePort} (save file) -> {@code OcrPort}
 * (image to text) -> {@code FacturaTextParser} (domain, text to fields) -> {@code GastoRepositoryPort} (save).
 */
public interface DigitalizarFacturaUseCase {

    Gasto digitalizar(ComandoDigitalizarFactura comando);

    /**
     * {@code contenido} is a raw byte[], not a MultipartFile: application ports must never
     * depend on a framework type, so the controller converts before calling in.
     */
    record ComandoDigitalizarFactura(
            Long usuarioId,
            byte[] contenido,
            String nombreOriginal,
            String contentType
    ) {}

}
