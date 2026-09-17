package com.fabio.GestionFacturas.application.gasto.service;

import com.fabio.GestionFacturas.application.gasto.port.in.ConsultarGastosUseCase;
import com.fabio.GestionFacturas.application.gasto.port.out.GastoRepositoryPort;
import com.fabio.GestionFacturas.domain.gasto.Gasto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/** Implements the read-side {@link ConsultarGastosUseCase}, backed by {@link GastoRepositoryPort}. */
@Service
public class
ConsultarGastosService implements ConsultarGastosUseCase {


    private final GastoRepositoryPort gastoRepository;

    public ConsultarGastosService(GastoRepositoryPort gastoRepository) {
        this.gastoRepository = gastoRepository;
    }

    @Override
    @Transactional(readOnly = true) // hints the DB driver/JPA no dirty-checking or write locks are needed
    public List<Gasto> listarPorUsuario(Long usuarioId) {
        return gastoRepository.buscarPorUsuario(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Gasto> obtenerPorId(Long gastoId, Long usuarioId) {
        Optional<Gasto> gasto = gastoRepository.buscarPorId(gastoId);
        if (gasto.isEmpty()){
            return Optional.empty();
        }
        // Access control: a gasto that exists but belongs to someone else is reported as absent, not forbidden
        if (!gasto.get().getUsuarioId().equals(usuarioId)){
            return Optional.empty();
        }
        return gasto;
    }


}
