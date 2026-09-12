package com.fabio.GestionFacturas.domain.gasto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class FacturaTextParserTest {

    private final FacturaTextParser parser = new FacturaTextParser();

    @Test
    @DisplayName("parsea el formato básico: fecha ISO, decimales con punto, sin símbolo de moneda")
    void parseaFormatoBasico() {
        String texto = """
                Emisor: Iberdrola
                Fecha: 2026-01-15
                Base imponible: 100.00
                IVA: 21.00
                Total: 121.00
                """;

        DatosFacturaExtraidos datos = parser.parsear(texto);

        assertThat(datos.emisor()).isEqualTo("Iberdrola");
        assertThat(datos.fechaEmision()).isEqualTo(LocalDate.of(2026, 1, 15));
        assertThat(datos.baseImponible()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(datos.iva()).isEqualByComparingTo(new BigDecimal("21.00"));
        assertThat(datos.total()).isEqualByComparingTo(new BigDecimal("121.00"));
    }

    @Test
    @DisplayName("reconoce decimales con coma y separador de miles con punto")
    void reconoceDecimalConComaYMiles() {
        String texto = """
                Emisor: Proveedor SL
                Fecha: 2026-01-15
                Base imponible: 1.000,00
                IVA: 210,00
                Total: 1.234,56
                """;

        DatosFacturaExtraidos datos = parser.parsear(texto);

        assertThat(datos.total()).isEqualByComparingTo(new BigDecimal("1234.56"));
    }

    @Test
    @DisplayName("reconoce decimales con punto y sin separador de miles")
    void reconoceDecimalConPuntoSinMiles() {
        String texto = """
                Emisor: Proveedor SL
                Fecha: 2026-01-15
                Base imponible: 1000.00
                IVA: 200.00
                Total: 1234.56
                """;

        DatosFacturaExtraidos datos = parser.parsear(texto);

        assertThat(datos.total()).isEqualByComparingTo(new BigDecimal("1234.56"));
    }

    @Test
    @DisplayName("reconoce la etiqueta TOTAL en mayúsculas")
    void reconoceEtiquetaTotalEnMayusculas() {
        String texto = """
                Emisor: Proveedor SL
                Fecha: 2026-01-15
                TOTAL: 121.00
                """;

        DatosFacturaExtraidos datos = parser.parsear(texto);

        assertThat(datos.total()).isEqualByComparingTo(new BigDecimal("121.00"));
    }

    @Test
    @DisplayName("reconoce la etiqueta 'Importe total'")
    void reconoceEtiquetaImporteTotal() {
        String texto = """
                Emisor: Proveedor SL
                Fecha: 2026-01-15
                Importe total: 121.00
                """;

        DatosFacturaExtraidos datos = parser.parsear(texto);

        assertThat(datos.total()).isEqualByComparingTo(new BigDecimal("121.00"));
    }

    @Test
    @DisplayName("reconoce la etiqueta 'A pagar'")
    void reconoceEtiquetaAPagar() {
        String texto = """
                Emisor: Proveedor SL
                Fecha: 2026-01-15
                A pagar: 121.00
                """;

        DatosFacturaExtraidos datos = parser.parsear(texto);

        assertThat(datos.total()).isEqualByComparingTo(new BigDecimal("121.00"));
    }

    @Test
    @DisplayName("reconoce la fecha en formato dd/MM/yyyy")
    void reconoceFechaConBarras() {
        String texto = """
                Emisor: Proveedor SL
                Fecha: 15/01/2026
                Total: 121.00
                """;

        DatosFacturaExtraidos datos = parser.parsear(texto);

        assertThat(datos.fechaEmision()).isEqualTo(LocalDate.of(2026, 1, 15));
    }

    @Test
    @DisplayName("reconoce la fecha en formato dd-MM-yyyy")
    void reconoceFechaConGuiones() {
        String texto = """
                Emisor: Proveedor SL
                Fecha: 15-01-2026
                Total: 121.00
                """;

        DatosFacturaExtraidos datos = parser.parsear(texto);

        assertThat(datos.fechaEmision()).isEqualTo(LocalDate.of(2026, 1, 15));
    }

    @Test
    @DisplayName("reconoce importes con el símbolo de moneda €")
    void reconoceImporteConSimboloEuro() {
        String texto = """
                Emisor: Proveedor SL
                Fecha: 2026-01-15
                Base imponible: 100,00 €
                IVA: 21,00 €
                Total: 121,00 €
                """;

        DatosFacturaExtraidos datos = parser.parsear(texto);

        assertThat(datos.baseImponible()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(datos.iva()).isEqualByComparingTo(new BigDecimal("21.00"));
        assertThat(datos.total()).isEqualByComparingTo(new BigDecimal("121.00"));
    }

    @Test
    @DisplayName("deja el IVA nulo si falta esa línea, sin afectar al resto de campos")
    void devuelveNuloSiFaltaUnCampo() {
        String texto = """
                Emisor: Proveedor SL
                Fecha: 2026-01-15
                Base imponible: 100.00
                Total: 121.00
                """;

        DatosFacturaExtraidos datos = parser.parsear(texto);

        assertThat(datos.iva()).isNull();
        assertThat(datos.emisor()).isEqualTo("Proveedor SL");
        assertThat(datos.fechaEmision()).isEqualTo(LocalDate.of(2026, 1, 15));
        assertThat(datos.baseImponible()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(datos.total()).isEqualByComparingTo(new BigDecimal("121.00"));
    }

    @Test
    @DisplayName("devuelve todos los campos nulos si el texto no tiene ninguna etiqueta reconocible")
    void devuelveTodosLosCamposNulosSiNoHayEtiquetas() {
        String texto = "documento ilegible sin estructura reconocible";

        DatosFacturaExtraidos datos = parser.parsear(texto);

        assertThat(datos.emisor()).isNull();
        assertThat(datos.fechaEmision()).isNull();
        assertThat(datos.baseImponible()).isNull();
        assertThat(datos.iva()).isNull();
        assertThat(datos.total()).isNull();
    }
}
