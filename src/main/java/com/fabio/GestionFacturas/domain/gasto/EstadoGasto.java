package com.fabio.GestionFacturas.domain.gasto;

/** Lifecycle states a Gasto moves through, from OCR/manual entry to being handed to the accountant. */
public enum EstadoGasto {

    BORRADOR, //Gasto recién llegado, aún sin revisar
    PROCESADA, //OCR/parsing hecho, pendiente de revisión
    REVISADA,   //el usuario confirmó los datos
    EXPORTADA   //gasto revisado, confirmado y exportado al gestor
}
