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

/**
 * Implements the {@link DigitalizarFacturaUseCase} inbound port and orchestrates the full
 * invoice-digitization workflow by composing three outbound ports plus one domain service,
 * each independently swappable behind its interface:
 * <pre>
 *   controller -> this service -> FileStoragePort   (save the raw file;    prod: LocalStorageAdapter)
 *                               -> OcrPort           (image -> plain text; prod: TesseractOcrAdapter, dev/tests: MockOcrAdapter)
 *                               -> FacturaTextParser (text -> fields;      pure domain logic, no adapter)
 *                               -> GastoRepositoryPort (persist the draft; prod: GastoPersistenceAdapter)
 * </pre>
 * OCR only turns pixels into text; it never parses fields, which is why extraction is a
 * separate domain step and not part of OcrPort itself.
 */
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
        // Step 1: persist the uploaded file, get back a storage reference (not the bytes) to keep on the Gasto
        String referencia = fileStoragePort.guardar(comando.contenido(), comando.nombreOriginal(), comando.contentType());

        // Step 2: OCR adapter converts the image to plain text (which adapter runs depends on the active Spring profile)
        String textoOcr = ocrPort.extraerTexto(comando.contenido());

        // Step 3: framework-free domain parsing turns that text into candidate fields
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

        // Step 4: build the domain object in BORRADOR — the user reviews/corrects fields afterwards via revisar()
        Gasto gasto = Gasto.crearBorradorDesdeArchivo(comando.usuarioId(), datos.emisor(), datos.fechaEmision(),
                baseImponible, iva, total, referencia);

        return gastoRepositoryPort.guardar(gasto);
    }
}
