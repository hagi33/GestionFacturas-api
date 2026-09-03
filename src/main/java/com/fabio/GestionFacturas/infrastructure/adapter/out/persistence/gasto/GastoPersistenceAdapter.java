package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.gasto;


import com.fabio.GestionFacturas.application.gasto.port.out.GastoRepositoryPort;
import com.fabio.GestionFacturas.domain.gasto.Gasto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class GastoPersistenceAdapter implements GastoRepositoryPort {

    private final GastoJpaRepository gastoJpaRepository;


    public GastoPersistenceAdapter(GastoJpaRepository gastoJpaRepository) {
        this.gastoJpaRepository = gastoJpaRepository;
    }

    @Override
    public Gasto guardar(Gasto gasto) {
        GastoJpaEntity entidad = GastoMapper.aEntidad(gasto);
        GastoJpaEntity guardada = gastoJpaRepository.save(entidad);
        return GastoMapper.aDominio(guardada);
    }

    @Override
    public Optional<Gasto> buscarPorId(Long id) {
        return gastoJpaRepository.findById(id).map(GastoMapper::aDominio);
    }

    @Override
    public List<Gasto> buscarPorUsuario(Long usuarioId) {
        return gastoJpaRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(GastoMapper::aDominio)
                .toList();
    }
}
