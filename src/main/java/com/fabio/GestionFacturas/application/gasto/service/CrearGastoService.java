package com.fabio.GestionFacturas.application.gasto.service;

import com.fabio.GestionFacturas.application.gasto.port.in.CrearGastoUseCase;
import com.fabio.GestionFacturas.application.gasto.port.out.GastoRepositoryPort;
import com.fabio.GestionFacturas.domain.gasto.Gasto;
import com.fabio.GestionFacturas.domain.shared.Dinero;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CrearGastoService implements CrearGastoUseCase {

    private final GastoRepositoryPort gastoRepositoryPort;

    public CrearGastoService(GastoRepositoryPort gastoRepositoryPort) {
        this.gastoRepositoryPort = gastoRepositoryPort;
    }

    @Override
    @Transactional
    public Gasto crear(ComandoCrearGasto comando) {
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

        Gasto gasto = Gasto.crearBorrador(comando.usuarioId(), comando.emisor(),
                comando.fechaEmision(), baseImponible, iva, total);

        return gastoRepositoryPort.guardar(gasto);
    }
}
