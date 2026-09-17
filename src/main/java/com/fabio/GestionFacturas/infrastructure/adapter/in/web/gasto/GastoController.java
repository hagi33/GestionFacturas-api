package com.fabio.GestionFacturas.infrastructure.adapter.in.web.gasto;

import com.fabio.GestionFacturas.application.gasto.port.in.ConsultarGastosUseCase;
import com.fabio.GestionFacturas.application.gasto.port.in.CrearGastoUseCase;
import com.fabio.GestionFacturas.application.gasto.port.in.DigitalizarFacturaUseCase;
import com.fabio.GestionFacturas.application.gasto.port.in.DigitalizarFacturaUseCase.ComandoDigitalizarFactura;
import com.fabio.GestionFacturas.domain.gasto.Gasto;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.gasto.dto.CrearGastoRequest;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.gasto.dto.GastoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Web adapter (inbound) for expenses. Depends only on the three inbound use-case ports below —
 * never on the services or repositories behind them. {@code GastoWebMapper} converts between
 * request/response DTOs and domain objects, so a {@link Gasto} never crosses the HTTP boundary directly.
 * {@code usuarioId} always comes from {@code @AuthenticationPrincipal}, set by {@code JwtAuthenticationFilter}
 * from the validated JWT — it's never trusted from the request body/params.
 */
@RestController
@RequestMapping("/api/gastos")
public class GastoController {

    private static final Set<String> TIPOS_PERMITIDOS =
            Set.of("application/pdf", "image/jpeg", "image/png", "image/webp");

    private final CrearGastoUseCase crearGastoUseCase;
    private final ConsultarGastosUseCase consultarGastosUseCase;
    private final DigitalizarFacturaUseCase digitalizarFacturaUseCase;

    public GastoController(CrearGastoUseCase crearGastoUseCase, ConsultarGastosUseCase consultarGastosUseCase,
                           DigitalizarFacturaUseCase digitalizarFacturaUseCase) {
        this.crearGastoUseCase = crearGastoUseCase;
        this.consultarGastosUseCase = consultarGastosUseCase;
        this.digitalizarFacturaUseCase = digitalizarFacturaUseCase;
    }

    // request DTO -> command -> CrearGastoUseCase (port) -> CrearGastoService -> domain Gasto -> response DTO
    @PostMapping
    public ResponseEntity<GastoResponse> crear(@Valid @RequestBody CrearGastoRequest request,
                                               @AuthenticationPrincipal Long usuarioId) {
        Gasto gasto = crearGastoUseCase.crear(GastoWebMapper.aComando(request, usuarioId));
        return ResponseEntity.status(HttpStatus.CREATED).body(GastoWebMapper.aRespuesta(gasto));
    }

    @GetMapping
    public List<GastoResponse> listar(@AuthenticationPrincipal Long usuarioId) {
        return consultarGastosUseCase.listarPorUsuario(usuarioId)
                .stream()
                .map(GastoWebMapper::aRespuesta)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GastoResponse> obtenerPorId(@PathVariable Long id,
                                                      @AuthenticationPrincipal Long usuarioId) {
        Optional<Gasto> gasto = consultarGastosUseCase.obtenerPorId(id, usuarioId);
        if (gasto.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(GastoWebMapper.aRespuesta(gasto.get()));
    }

    @PostMapping(path = "/digitalizar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GastoResponse> digitalizar(@RequestParam("archivo") MultipartFile archivo,
                                                      @AuthenticationPrincipal Long usuarioId) {
        if (archivo.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        }

        String contentType = archivo.getContentType();
        if (!TIPOS_PERMITIDOS.contains(contentType)) {
            throw new IllegalArgumentException("Tipo de archivo no soportado: " + contentType);
        }

        // MultipartFile is a framework type — converted to byte[] right here, since application
        // ports must never depend on Spring MVC types (see ComandoDigitalizarFactura's Javadoc).
        byte[] contenido;
        try {
            contenido = archivo.getBytes();
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer el archivo subido", e);
        }

        ComandoDigitalizarFactura comando = new ComandoDigitalizarFactura(
                usuarioId, contenido, archivo.getOriginalFilename(), contentType);

        // Kicks off the full OCR pipeline in DigitalizarFacturaService (storage -> OCR -> parse -> save)
        Gasto gasto = digitalizarFacturaUseCase.digitalizar(comando);

        return ResponseEntity.status(HttpStatus.CREATED).body(GastoWebMapper.aRespuesta(gasto));
    }
}
