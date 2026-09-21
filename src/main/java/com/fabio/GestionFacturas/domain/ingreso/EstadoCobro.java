package com.fabio.GestionFacturas.domain.ingreso;

/** Payment state of an Ingreso: whether the invoice issued to the client has been collected. */
public enum EstadoCobro {

    PENDIENTE, //factura emitida, aún no cobrada
    COBRADA    //el usuario registró el cobro
}
