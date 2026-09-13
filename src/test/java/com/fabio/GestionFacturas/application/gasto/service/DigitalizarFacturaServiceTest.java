package com.fabio.GestionFacturas.application.gasto.service;

import com.fabio.GestionFacturas.application.gasto.port.in.DigitalizarFacturaUseCase.ComandoDigitalizarFactura;
import com.fabio.GestionFacturas.application.gasto.port.out.GastoRepositoryPort;
import com.fabio.GestionFacturas.application.shared.port.out.FileStoragePort;
import com.fabio.GestionFacturas.application.shared.port.out.OcrPort;
import com.fabio.GestionFacturas.domain.gasto.DatosFacturaExtraidos;
import com.fabio.GestionFacturas.domain.gasto.EstadoGasto;
import com.fabio.GestionFacturas.domain.gasto.FacturaTextParser;
import com.fabio.GestionFacturas.domain.gasto.Gasto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DigitalizarFacturaServiceTest {

    @Mock
    private FileStoragePort fileStoragePort;
    @Mock
    private OcrPort ocrPort;
    @Mock
    private FacturaTextParser facturaTextParser;
    @Mock
    private GastoRepositoryPort gastoRepositoryPort;

    @InjectMocks
    private DigitalizarFacturaService service;

    @Test
    @DisplayName("almacena el archivo, extrae el texto, lo parsea y guarda un gasto en BORRADOR")
    void digitalizaCorrectamenteConDatosCompletos(){
        //Arrange
        byte[] contenido = "contenido-de-prueba".getBytes();
        ComandoDigitalizarFactura comando = new ComandoDigitalizarFactura(1L, contenido, "factura.pdf", "application/pdf");

        when(fileStoragePort.guardar(contenido, "factura.pdf", "application/pdf")).thenReturn("uuid-referencia.pdf");
        when(ocrPort.extraerTexto(contenido)).thenReturn("texto-ocr-simulado");
        when(facturaTextParser.parsear("texto-ocr-simulado")).thenReturn(new DatosFacturaExtraidos(
                "Iberdrola", LocalDate.of(2026, 1, 15),
                new BigDecimal("100.00"), new BigDecimal("21.00"), new BigDecimal("121.00")
        ));
        when(gastoRepositoryPort.guardar(any(Gasto.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        //Act
        Gasto resultado = service.digitalizar(comando);

        //Assert
        assertThat(resultado.getEstado()).isEqualTo(EstadoGasto.BORRADOR);
        assertThat(resultado.getUsuarioId()).isEqualTo(1L);
        assertThat(resultado.getEmisor()).isEqualTo("Iberdrola");
        assertThat(resultado.getFechaEmision()).isEqualTo(LocalDate.of(2026, 1, 15));
        assertThat(resultado.getBaseImponible().cantidad()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(resultado.getIva().cantidad()).isEqualByComparingTo(new BigDecimal("21.00"));
        assertThat(resultado.getTotal().cantidad()).isEqualByComparingTo(new BigDecimal("121.00"));

        verify(fileStoragePort).guardar(contenido, "factura.pdf", "application/pdf");
        verify(ocrPort).extraerTexto(contenido);
        verify(facturaTextParser).parsear("texto-ocr-simulado");
        verify(gastoRepositoryPort).guardar(any(Gasto.class));
    }

    @Test
    @DisplayName("deja los campos no extraídos en null sin fallar")
    void digitalizaConCamposFaltantesQuedaConNulos(){
        //Arrange
        byte[] contenido = "contenido-de-prueba".getBytes();
        ComandoDigitalizarFactura comando = new ComandoDigitalizarFactura(1L, contenido, "factura.jpg", "image/jpeg");

        when(fileStoragePort.guardar(any(), any(), any())).thenReturn("uuid-referencia.jpg");
        when(ocrPort.extraerTexto(contenido)).thenReturn("texto-ocr-parcial");
        when(facturaTextParser.parsear("texto-ocr-parcial")).thenReturn(new DatosFacturaExtraidos(
                "Proveedor SL", LocalDate.of(2026, 1, 15),
                new BigDecimal("100.00"), null, new BigDecimal("121.00")
        ));
        when(gastoRepositoryPort.guardar(any(Gasto.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        //Act
        Gasto resultado = service.digitalizar(comando);

        //Assert
        assertThat(resultado.getIva()).isNull();
        assertThat(resultado.getEmisor()).isEqualTo("Proveedor SL");
        assertThat(resultado.getBaseImponible().cantidad()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(resultado.getTotal().cantidad()).isEqualByComparingTo(new BigDecimal("121.00"));
    }
}
