package com.fabio.GestionFacturas.infrastructure.adapter.in.web.gasto;

import com.fabio.GestionFacturas.application.gasto.port.in.ConsultarGastosUseCase;
import com.fabio.GestionFacturas.application.gasto.port.in.CrearGastoUseCase;
import com.fabio.GestionFacturas.domain.gasto.Gasto;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.gasto.dto.CrearGastoRequest;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.gasto.dto.GastoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/gastos")
public class GastoController {

    // Fase 0: usuarioId hardcodeado a 1L; se sustituirá por el usuario autenticado vía JWT en Fase 1
    private static final Long USUARIO_ID_TEMPORAL = 1L;

    private final CrearGastoUseCase crearGastoUseCase;
    private final ConsultarGastosUseCase consultarGastosUseCase;

    public GastoController(CrearGastoUseCase crearGastoUseCase, ConsultarGastosUseCase consultarGastosUseCase) {
        this.crearGastoUseCase = crearGastoUseCase;
        this.consultarGastosUseCase = consultarGastosUseCase;
    }

    @PostMapping
    public ResponseEntity<GastoResponse> crear(@Valid @RequestBody CrearGastoRequest request) {
        Gasto gasto = crearGastoUseCase.crear(GastoWebMapper.aComando(request, USUARIO_ID_TEMPORAL));
        return ResponseEntity.status(HttpStatus.CREATED).body(GastoWebMapper.aRespuesta(gasto));
    }

    @GetMapping
    public List<GastoResponse> listar() {
        return consultarGastosUseCase.listarPorUsuario(USUARIO_ID_TEMPORAL)
                .stream()
                .map(GastoWebMapper::aRespuesta)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GastoResponse> obtenerPorId(@PathVariable Long id) {
        Optional<Gasto> gasto = consultarGastosUseCase.obtenerPorId(id, USUARIO_ID_TEMPORAL);
        if (gasto.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(GastoWebMapper.aRespuesta(gasto.get()));
    }
}
