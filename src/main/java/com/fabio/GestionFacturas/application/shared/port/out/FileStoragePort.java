package com.fabio.GestionFacturas.application.shared.port.out;

public interface FileStoragePort {

    String guardar(byte[] contenido, String nombreOriginal, String contentType);

    byte[] recuperar(String referencia);

    void eliminar(String referencia);

}
