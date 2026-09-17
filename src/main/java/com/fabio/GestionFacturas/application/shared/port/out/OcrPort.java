package com.fabio.GestionFacturas.application.shared.port.out;

/**
 * Outbound port: image bytes -> raw text, nothing more. Two adapters implement it —
 * {@code MockOcrAdapter} (default bean, dev/tests) and {@code TesseractOcrAdapter}
 * (behind the "ocr" Spring profile) — swappable without the service knowing which is active.
 */
public interface OcrPort {

    String extraerTexto(byte[] contenido);

}
