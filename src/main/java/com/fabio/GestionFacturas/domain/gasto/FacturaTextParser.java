package com.fabio.GestionFacturas.domain.gasto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FacturaTextParser {

    private static final Pattern PATRON_EMISOR = Pattern.compile("(?im)^\\s*Emisor\\s*:\\s*(.+)$");
    private static final Pattern PATRON_FECHA = Pattern.compile("(?im)^\\s*Fecha\\s*:\\s*(.+)$");
    private static final Pattern PATRON_BASE_IMPONIBLE = Pattern.compile("(?im)^\\s*Base imponible\\s*:\\s*(.+)$");
    private static final Pattern PATRON_IVA = Pattern.compile("(?im)^\\s*IVA\\s*:\\s*(.+)$");
    private static final Pattern PATRON_TOTAL = Pattern.compile("(?im)^\\s*(?:Total|Importe total|A pagar)\\s*:\\s*(.+)$");

    private static final List<DateTimeFormatter> FORMATOS_FECHA = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy")
    );

    public DatosFacturaExtraidos parsear(String textoOcr) {
        if (textoOcr == null) {
            return new DatosFacturaExtraidos(null, null, null, null, null);
        }

        String emisor = extraerValor(PATRON_EMISOR, textoOcr);
        LocalDate fechaEmision = extraerFecha(textoOcr);
        BigDecimal baseImponible = extraerImporte(PATRON_BASE_IMPONIBLE, textoOcr);
        BigDecimal iva = extraerImporte(PATRON_IVA, textoOcr);
        BigDecimal total = extraerImporte(PATRON_TOTAL, textoOcr);

        return new DatosFacturaExtraidos(emisor, fechaEmision, baseImponible, iva, total);
    }

    private String extraerValor(Pattern patron, String texto) {
        Matcher matcher = patron.matcher(texto);
        if (!matcher.find()) {
            return null;
        }
        return matcher.group(1).trim();
    }

    private LocalDate extraerFecha(String texto) {
        String valor = extraerValor(PATRON_FECHA, texto);
        if (valor == null) {
            return null;
        }

        for (DateTimeFormatter formato : FORMATOS_FECHA) {
            try {
                return LocalDate.parse(valor, formato);
            } catch (DateTimeParseException e) {
                // se intenta con el siguiente formato soportado
            }
        }

        return null;
    }

    private BigDecimal extraerImporte(Pattern patron, String texto) {
        String valor = extraerValor(patron, texto);
        if (valor == null) {
            return null;
        }

        String normalizado = normalizarImporte(valor);

        try {
            return new BigDecimal(normalizado);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String normalizarImporte(String valorBruto) {
        String limpio = valorBruto.replace("€", "").trim();

        if (limpio.contains(",")) {
            limpio = limpio.replace(".", "");
            limpio = limpio.replace(",", ".");
        }

        return limpio;
    }
}
