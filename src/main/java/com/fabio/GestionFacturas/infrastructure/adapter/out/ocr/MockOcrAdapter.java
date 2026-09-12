package com.fabio.GestionFacturas.infrastructure.adapter.out.ocr;

import com.fabio.GestionFacturas.application.shared.port.out.OcrPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!tesseract")
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
