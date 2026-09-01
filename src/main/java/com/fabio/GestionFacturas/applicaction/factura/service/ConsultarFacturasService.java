package com.fabio.GestionFacturas.applicaction.factura.service;

import com.fabio.GestionFacturas.applicaction.factura.port.in.ConsultarFacturasUseCase;
import com.fabio.GestionFacturas.applicaction.factura.port.out.FacturaRepositoryPort;
import com.fabio.GestionFacturas.domain.factura.Factura;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ConsultarFacturasService implements ConsultarFacturasUseCase {


    private final FacturaRepositoryPort facturaRepository;

    public ConsultarFacturasService(FacturaRepositoryPort facturaRepository) {
        this.facturaRepository = facturaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Factura> listarPorUsuario(Long usuarioId) {
        return facturaRepository.buscarPorUsuario(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Factura> obtenerPorId(Long facturaId, Long usuarioId) {
        Optional<Factura> factura = facturaRepository.buscarPorId(facturaId);
        if (factura.isPresent()){
            return Optional.empty();
        }
        if (!factura.get().getUsuarioId().equals(usuarioId)){
            return Optional.empty();
        }
        return factura;
    }


}
