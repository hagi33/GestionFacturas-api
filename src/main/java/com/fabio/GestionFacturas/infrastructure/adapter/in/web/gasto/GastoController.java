package com.fabio.GestionFacturas.infrastructure.adapter.in.web.gasto;

import com.fabio.GestionFacturas.application.gasto.port.in.ConsultarGastosUseCase;
import com.fabio.GestionFacturas.application.gasto.port.in.CrearGastoUseCase;
import com.fabio.GestionFacturas.domain.gasto.Gasto;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.gasto.dto.CrearGastoRequest;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.gasto.dto.GastoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    private final CrearGastoUseCase crearGastoUseCase;
    private final ConsultarGastosUseCase consultarGastosUseCase;

    public GastoController(CrearGastoUseCase crearGastoUseCase, ConsultarGastosUseCase consultarGastosUseCase) {
        this.crearGastoUseCase = crearGastoUseCase;
        this.consultarGastosUseCase = consultarGastosUseCase;
    }

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
}
