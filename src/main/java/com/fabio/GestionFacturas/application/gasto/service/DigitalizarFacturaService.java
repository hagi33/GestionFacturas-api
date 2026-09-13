package com.fabio.GestionFacturas.application.gasto.service;

import com.fabio.GestionFacturas.application.gasto.port.in.DigitalizarFacturaUseCase;
import com.fabio.GestionFacturas.application.gasto.port.out.GastoRepositoryPort;
import com.fabio.GestionFacturas.application.shared.port.out.FileStoragePort;
import com.fabio.GestionFacturas.application.shared.port.out.OcrPort;
import com.fabio.GestionFacturas.domain.gasto.DatosFacturaExtraidos;
import com.fabio.GestionFacturas.domain.gasto.FacturaTextParser;
import com.fabio.GestionFacturas.domain.gasto.Gasto;
import com.fabio.GestionFacturas.domain.shared.Dinero;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DigitalizarFacturaService implements DigitalizarFacturaUseCase {

    private final FileStoragePort fileStoragePort;
    private final OcrPort ocrPort;
    private final FacturaTextParser facturaTextParser;
    private final GastoRepositoryPort gastoRepositoryPort;

    public DigitalizarFacturaService(FileStoragePort fileStoragePort,
                                     OcrPort ocrPort,
                                     FacturaTextParser facturaTextParser,
                                     GastoRepositoryPort gastoRepositoryPort) {
        this.fileStoragePort = fileStoragePort;
        this.ocrPort = ocrPort;
        this.facturaTextParser = facturaTextParser;
        this.gastoRepositoryPort = gastoRepositoryPort;
    }

    @Override
    @Transactional
    public Gasto digitalizar(ComandoDigitalizarFactura comando) {
        String referencia = fileStoragePort.guardar(comando.contenido(), comando.nombreOriginal(), comando.contentType());

        String textoOcr = ocrPort.extraerTexto(comando.contenido());

        DatosFacturaExtraidos datos = facturaTextParser.parsear(textoOcr);

        Dinero baseImponible = null;
        if (datos.baseImponible() != null) {
            baseImponible = Dinero.deEuros(datos.baseImponible());
        }

        Dinero iva = null;
        if (datos.iva() != null) {
            iva = Dinero.deEuros(datos.iva());
        }

        Dinero total = null;
        if (datos.total() != null) {
            total = Dinero.deEuros(datos.total());
        }

        Gasto gasto = Gasto.crearBorradorDesdeArchivo(comando.usuarioId(), datos.emisor(), datos.fechaEmision(),
                baseImponible, iva, total, referencia);

        return gastoRepositoryPort.guardar(gasto);
    }
}
