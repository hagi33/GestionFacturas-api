package com.fabio.GestionFacturas.application.shared.port.out;

/**
 * Outbound port for storing/retrieving uploaded files. Implemented today by
 * {@code LocalStorageAdapter} (filesystem); a MinIO-backed adapter is planned as a drop-in
 * replacement — callers only ever see the opaque reference string, never a path.
 */
public interface FileStoragePort {

    /** @return an opaque reference (not a path) to persist on the Gasto, used later to retrieve/delete the file */
    String guardar(byte[] contenido, String nombreOriginal, String contentType);

    byte[] recuperar(String referencia);

    void eliminar(String referencia);

}
