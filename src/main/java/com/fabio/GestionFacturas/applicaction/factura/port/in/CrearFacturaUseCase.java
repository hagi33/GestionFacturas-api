package com.fabio.GestionFacturas.applicaction.factura.port.in;

import com.fabio.GestionFacturas.domain.factura.Factura;
import org.hibernate.id.BulkInsertionCapableIdentifierGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface CrearFacturaUseCase {

    Factura crear(ComandoCrearFactura comando);

    record ComandoCrearFactura(
            Long usuarioId,
            String emisor,
            LocalDate fechaEmision,
            BigDecimal baseImponible,
            BigDecimal iva,
            BigDecimal total,
            String moneda

    ){}


    }

