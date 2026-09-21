package com.fabio.GestionFacturas.application.gasto.service;

import com.fabio.GestionFacturas.application.cliente.port.out.ClienteRepositoryPort;
import com.fabio.GestionFacturas.application.gasto.port.in.CrearGastoUseCase;
import com.fabio.GestionFacturas.application.gasto.port.out.GastoRepositoryPort;
import com.fabio.GestionFacturas.domain.cliente.Cliente;
import com.fabio.GestionFacturas.domain.cliente.ClienteNoEncontradoException;
import com.fabio.GestionFacturas.domain.gasto.Gasto;
import com.fabio.GestionFacturas.domain.shared.Dinero;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implements the {@link CrearGastoUseCase} inbound port. Called by {@code GastoController};
 * talks only to {@link GastoRepositoryPort} (outbound port), never to the JPA adapter directly —
 * that indirection is what lets the persistence technology change without touching this class.
 */
@Service
public class CrearGastoService implements CrearGastoUseCase {

    private final GastoRepositoryPort gastoRepositoryPort;
    private final ClienteRepositoryPort clienteRepositoryPort;

    // Constructor injection only (project convention) — no @Autowired fields, easier to unit test with mocks.
    public CrearGastoService(GastoRepositoryPort gastoRepositoryPort, ClienteRepositoryPort clienteRepositoryPort) {
        this.gastoRepositoryPort = gastoRepositoryPort;
        this.clienteRepositoryPort = clienteRepositoryPort;
    }

    @Override
    @Transactional // wraps the save in a DB transaction; matters more once this method touches >1 aggregate
    public Gasto crear(ComandoCrearGasto comando) {
        if (comando.clienteId() != null) {
            validarCliente(comando.clienteId(), comando.usuarioId());
        }

        String moneda = comando.moneda();
        if (moneda == null) {
            moneda = "EUR";
        }

        Dinero baseImponible = null;
        if (comando.baseImponible() != null) {
            baseImponible = new Dinero(comando.baseImponible(), moneda);
        }

        Dinero iva = null;
        if (comando.iva() != null) {
            iva = new Dinero(comando.iva(), moneda);
        }

        Dinero total = null;
        if (comando.total() != null) {
            total = new Dinero(comando.total(), moneda);
        }

        Gasto gasto = Gasto.crearBorrador(comando.usuarioId(), comando.clienteId(), comando.emisor(),
                comando.fechaEmision(), baseImponible, iva, total);

        return gastoRepositoryPort.guardar(gasto);
    }

    // Same message for not-found/wrong-owner/inactive so none is distinguishable from outside
    // (mirrors DesactivarClienteService/RegistrarCobroService's existence-hiding convention).
    private void validarCliente(Long clienteId, Long usuarioId) {
        Optional<Cliente> clienteOpt = clienteRepositoryPort.buscarPorId(clienteId);
        if (clienteOpt.isEmpty()) {
            throw new ClienteNoEncontradoException("Cliente no encontrado");
        }

        Cliente cliente = clienteOpt.get();

        if (!cliente.getUsuarioId().equals(usuarioId)) {
            throw new ClienteNoEncontradoException("Cliente no encontrado");
        }

        if (!cliente.isActivo()) {
            throw new ClienteNoEncontradoException("Cliente no encontrado");
        }
    }
}
