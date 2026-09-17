package com.fabio.GestionFacturas.infrastructure.adapter.out.ocr;

import com.fabio.GestionFacturas.application.shared.port.out.OcrPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Implements {@link OcrPort} with fixed sample text instead of a real OCR engine — the
 * default bean (active whenever the "ocr" profile is NOT set), so dev and tests exercise the
 * full digitization flow without needing Tesseract installed. See {@link TesseractOcrAdapter}
 * for the real implementation.
 */
//Ocr Adapter simulado cuando la app es ejecutada con un perfil distinto a ocr
@Component
@Profile("!ocr")
public class MockOcrAdapter implements OcrPort {

    private static final String TEXTO_FACTURA_SIMULADA = """
            Emisor: Iberdrola Comercializacion S.A.U.
            Fecha: 15/01/2026
            Base imponible: 100,00 €
            IVA: 21,00 €
            Total: 121,00 €
            """;

    @Override
    public String extraerTexto(byte[] contenido) {
        return TEXTO_FACTURA_SIMULADA;
    }
}
