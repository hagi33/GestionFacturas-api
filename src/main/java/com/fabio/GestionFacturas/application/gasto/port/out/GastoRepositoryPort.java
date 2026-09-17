package com.fabio.GestionFacturas.application.gasto.port.out;

import com.fabio.GestionFacturas.domain.gasto.Gasto;

import java.util.List;
import java.util.Optional;

/**
 * Outbound port for Gasto persistence. Defined here (application layer) so services depend
 * only on this contract; {@code GastoPersistenceAdapter} (infrastructure) implements it with JPA,
 * keeping the dependency pointing inward per the hexagonal dependency rule.
 */
public interface GastoRepositoryPort {

    Gasto guardar(Gasto gasto);

    Optional <Gasto> buscarPorId(Long id);

    List<Gasto> buscarPorUsuario(Long usuarioId);

}
