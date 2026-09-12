package com.fabio.GestionFacturas.application.shared.port.out;

public interface OcrPort {

    String extraerTexto(byte[] contenido);

}
