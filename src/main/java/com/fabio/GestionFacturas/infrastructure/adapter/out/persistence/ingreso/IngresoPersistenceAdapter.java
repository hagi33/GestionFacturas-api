package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.ingreso;

import com.fabio.GestionFacturas.application.ingreso.port.out.IngresoRepositoryPort;
import com.fabio.GestionFacturas.domain.ingreso.Ingreso;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Implements the outbound {@link IngresoRepositoryPort} with Spring Data JPA — the piece the
 * application layer never sees directly. Full round trip for a write:
 * domain Ingreso -> IngresoMapper -> IngresoJpaEntity -> IngresoJpaRepository (Spring Data) -> DB,
 * then mapped straight back to domain before returning, so callers never touch the JPA type.
 */
@Component
public class IngresoPersistenceAdapter implements IngresoRepositoryPort {

    private final IngresoJpaRepository ingresoJpaRepository;

    public IngresoPersistenceAdapter(IngresoJpaRepository ingresoJpaRepository) {
        this.ingresoJpaRepository = ingresoJpaRepository;
    }

    @Override
    public Ingreso guardar(Ingreso ingreso) {
        IngresoJpaEntity entidad = IngresoMapper.aEntidad(ingreso);
        IngresoJpaEntity guardado = ingresoJpaRepository.save(entidad);
        return IngresoMapper.aDominio(guardado);
    }

    @Override
    public Optional<Ingreso> buscarPorId(Long id) {
        return ingresoJpaRepository.findById(id).map(IngresoMapper::aDominio);
    }

    @Override
    public List<Ingreso> buscarPorUsuario(Long usuarioId) {
        return ingresoJpaRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(IngresoMapper::aDominio)
                .toList();
    }
}
