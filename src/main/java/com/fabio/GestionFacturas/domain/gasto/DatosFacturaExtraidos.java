package com.fabio.GestionFacturas.domain.gasto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Output of {@link FacturaTextParser}: fields extracted from raw OCR text.
 * Any field can be null when the parser couldn't find it — the user fills gaps in on review.
 */
public record DatosFacturaExtraidos(
        String emisor,
        LocalDate fechaEmision,
        BigDecimal baseImponible,
        BigDecimal iva,
        BigDecimal total
) {
}
