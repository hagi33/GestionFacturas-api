package com.fabio.GestionFacturas.domain.gasto;

public enum EstadoGasto {

    BORRADOR, //Gasto recién llegado, aún sin revisar
    PROCESADA, //OCR/parsing hecho, pendiente de revisión
    REVISADA,   //el usuario confirmó los datos
    EXPORTADA   //gasto revisado, confirmado y exportado al gestor
}
