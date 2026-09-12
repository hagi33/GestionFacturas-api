package com.fabio.GestionFacturas.domain.gasto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DatosFacturaExtraidos(
        String emisor,
        LocalDate fechaEmision,
        BigDecimal baseImponible,
        BigDecimal iva,
        BigDecimal total
) {
}
