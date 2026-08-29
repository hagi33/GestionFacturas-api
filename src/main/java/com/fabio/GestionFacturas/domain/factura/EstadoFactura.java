package com.fabio.GestionFacturas.domain.factura;

public enum EstadoFactura {

    BORRADOR, //Factura recién llegada, aún sin revisar
    PROCESADA, //OCR/parsing hecho, pendiente de revisión
    REVISADA,   //el usuario confirmó los datos
    EXPORTADA   //factura revisada, confirmada y exportada al gestor
}
