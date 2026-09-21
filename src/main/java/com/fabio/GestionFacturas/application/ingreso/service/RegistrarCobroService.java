package com.fabio.GestionFacturas.application.ingreso.service;

import com.fabio.GestionFacturas.application.ingreso.port.in.RegistrarCobroUseCase;
import com.fabio.GestionFacturas.application.ingreso.port.out.IngresoRepositoryPort;
import com.fabio.GestionFacturas.domain.ingreso.Ingreso;
import com.fabio.GestionFacturas.domain.ingreso.IngresoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/** Implements {@link RegistrarCobroUseCase}. */
@Service
public class RegistrarCobroService implements RegistrarCobroUseCase {

    private final IngresoRepositoryPort ingresoRepositoryPort;

    public RegistrarCobroService(IngresoRepositoryPort ingresoRepositoryPort) {
        this.ingresoRepositoryPort = ingresoRepositoryPort;
    }

    @Override
    @Transactional
    public Ingreso registrarCobro(ComandoRegistrarCobro comando) {
        Optional<Ingreso> ingresoOpt = ingresoRepositoryPort.buscarPorId(comando.ingresoId());
        if (ingresoOpt.isEmpty()) {
            throw new IngresoNoEncontradoException("El ingreso no existe");
        }

        Ingreso ingreso = ingresoOpt.get();

        // Access control: don't reveal that an ingreso exists if it belongs to someone else
        if (!ingreso.getUsuarioId().equals(comando.usuarioId())) {
            throw new IngresoNoEncontradoException("El ingreso no existe");
        }

        ingreso.registrarCobro(comando.fechaCobro());
        return ingresoRepositoryPort.guardar(ingreso);
    }
}
