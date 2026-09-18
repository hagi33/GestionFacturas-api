package com.fabio.GestionFacturas.infrastructure.adapter.in.web.cliente;

import com.fabio.GestionFacturas.application.cliente.port.in.ConsultarClientesUseCase;
import com.fabio.GestionFacturas.application.cliente.port.in.CrearClienteUseCase;
import com.fabio.GestionFacturas.application.cliente.port.in.DesactivarClienteUseCase;
import com.fabio.GestionFacturas.application.cliente.port.in.DesactivarClienteUseCase.ComandoDesactivarCliente;
import com.fabio.GestionFacturas.application.cliente.port.in.EditarClienteUseCase;
import com.fabio.GestionFacturas.domain.cliente.Cliente;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.cliente.dto.ClienteResponse;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.cliente.dto.CrearClienteRequest;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.cliente.dto.EditarClienteRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * Web adapter (inbound) for clients. Depends only on the four inbound use-case ports below —
 * never on the services or repositories behind them. {@code ClienteWebMapper} converts between
 * request/response DTOs and domain objects, so a {@link Cliente} never crosses the HTTP boundary
 * directly. {@code usuarioId} always comes from {@code @AuthenticationPrincipal}, set by
 * {@code JwtAuthenticationFilter} from the validated JWT — it's never trusted from the request.
 * <p>
 * POST and DELETE currently call TDD-stub services ({@code CrearClienteService},
 * {@code DesactivarClienteService}) and will throw {@code UnsupportedOperationException} until
 * those are implemented — that's expected at this stage.
 */
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final CrearClienteUseCase crearClienteUseCase;
    private final ConsultarClientesUseCase consultarClientesUseCase;
    private final EditarClienteUseCase editarClienteUseCase;
    private final DesactivarClienteUseCase desactivarClienteUseCase;

    public ClienteController(CrearClienteUseCase crearClienteUseCase,
                              ConsultarClientesUseCase consultarClientesUseCase,
                              EditarClienteUseCase editarClienteUseCase,
                              DesactivarClienteUseCase desactivarClienteUseCase) {
        this.crearClienteUseCase = crearClienteUseCase;
        this.consultarClientesUseCase = consultarClientesUseCase;
        this.editarClienteUseCase = editarClienteUseCase;
        this.desactivarClienteUseCase = desactivarClienteUseCase;
    }

    // request DTO -> command -> CrearClienteUseCase (port) -> CrearClienteService -> domain Cliente -> response DTO
    @PostMapping
    public ResponseEntity<ClienteResponse> crear(@Valid @RequestBody CrearClienteRequest request,
                                                  @AuthenticationPrincipal Long usuarioId) {
        Cliente cliente = crearClienteUseCase.crear(ClienteWebMapper.aComandoCrear(request, usuarioId));
        return ResponseEntity.status(HttpStatus.CREATED).body(ClienteWebMapper.aRespuesta(cliente));
    }

    @GetMapping
    public List<ClienteResponse> listar(@AuthenticationPrincipal Long usuarioId) {
        return consultarClientesUseCase.listarPorUsuario(usuarioId)
                .stream()
                .map(ClienteWebMapper::aRespuesta)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> obtenerPorId(@PathVariable Long id,
                                                         @AuthenticationPrincipal Long usuarioId) {
        Optional<Cliente> cliente = consultarClientesUseCase.obtenerPorId(id, usuarioId);
        if (cliente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ClienteWebMapper.aRespuesta(cliente.get()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> editar(@PathVariable Long id,
                                                   @Valid @RequestBody EditarClienteRequest request,
                                                   @AuthenticationPrincipal Long usuarioId) {
        Cliente cliente = editarClienteUseCase.editar(ClienteWebMapper.aComandoEditar(id, request, usuarioId));
        return ResponseEntity.ok(ClienteWebMapper.aRespuesta(cliente));
    }

    // Soft delete: DesactivarClienteUseCase flips activo=false, the row is kept
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id, @AuthenticationPrincipal Long usuarioId) {
        desactivarClienteUseCase.desactivar(new ComandoDesactivarCliente(id, usuarioId));
        return ResponseEntity.noContent().build();
    }
}
