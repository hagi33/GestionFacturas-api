package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.factura;


import com.fabio.GestionFacturas.applicaction.factura.port.out.FacturaRepositoryPort;
import com.fabio.GestionFacturas.domain.factura.Factura;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class FacturaPersistenceAdapter implements FacturaRepositoryPort {

    private final FacturaJpaRepository facturaJpaRepository;


    public FacturaPersistenceAdapter(FacturaJpaRepository facturaJpaRepository) {
        this.facturaJpaRepository = facturaJpaRepository;
    }

    @Override
    public Factura guardar(Factura factura) {
        FacturaJpaEntity entidad = FacturaMapper.aEntidad(factura);
        FacturaJpaEntity guardada = facturaJpaRepository.save(entidad);
        return FacturaMapper.aDominio(guardada);
    }

    @Override
    public Optional<Factura> buscarPorId(Long id) {
        return facturaJpaRepository.findById(id).map(FacturaMapper::aDominio);
    }

    @Override
    public List<Factura> buscarPorUsuario(Long usuarioId) {
        return facturaJpaRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(FacturaMapper::aDominio)
                .toList();
    }
}
