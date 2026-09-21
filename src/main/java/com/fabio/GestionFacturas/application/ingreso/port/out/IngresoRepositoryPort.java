package com.fabio.GestionFacturas.application.ingreso.port.out;

import com.fabio.GestionFacturas.domain.ingreso.Ingreso;

import java.util.List;
import java.util.Optional;

/**
 * Outbound port for Ingreso persistence. Defined here (application layer) so services depend
 * only on this contract; {@code IngresoPersistenceAdapter} (infrastructure) implements it with JPA,
 * keeping the dependency pointing inward per the hexagonal dependency rule.
 */
public interface IngresoRepositoryPort {

    Ingreso guardar(Ingreso ingreso);

    Optional<Ingreso> buscarPorId(Long id);

    List<Ingreso> buscarPorUsuario(Long usuarioId);

}
