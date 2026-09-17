package com.fabio.GestionFacturas.infrastructure.adapter.out.storage;

import com.fabio.GestionFacturas.application.shared.port.out.FileStoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Implements {@link FileStoragePort} on the local filesystem — today's only storage adapter
 * (a MinIO-backed one is planned as a drop-in replacement). {@code guardar} is called from
 * {@code DigitalizarFacturaService} as the first step of the OCR pipeline.
 */
@Component
public class LocalStorageAdapter implements FileStoragePort {

    private final Path basePath;

    public LocalStorageAdapter(@Value("${app.storage.local.base-path}") String basePath) {
        this.basePath = Paths.get(basePath);
        crearDirectorioBase();
    }

    private void crearDirectorioBase() {
        try {
            Files.createDirectories(basePath);
        } catch (IOException e) {
            throw new FileStorageException("No se pudo crear el directorio de almacenamiento: " + basePath, e);
        }
    }

    @Override
    public String guardar(byte[] contenido, String nombreOriginal, String contentType) {
        // The reference handed back and stored on the Gasto is a random filename, never the original one
        String referencia = UUID.randomUUID() + extraerExtension(nombreOriginal);
        Path destino = basePath.resolve(referencia);

        try {
            Files.write(destino, contenido);
        } catch (IOException e) {
            throw new FileStorageException("No se pudo guardar el archivo: " + nombreOriginal, e);
        }

        return referencia;
    }

    @Override
    public byte[] recuperar(String referencia) {
        Path archivo = basePath.resolve(referencia);

        if (!Files.exists(archivo)) {
            throw new FileStorageException("No existe ningún archivo con la referencia: " + referencia);
        }

        try {
            return Files.readAllBytes(archivo);
        } catch (IOException e) {
            throw new FileStorageException("No se pudo leer el archivo con referencia: " + referencia, e);
        }
    }

    @Override
    public void eliminar(String referencia) {
        Path archivo = basePath.resolve(referencia);

        try {
            Files.delete(archivo);
        } catch (IOException e) {
            throw new FileStorageException("No se pudo eliminar el archivo con referencia: " + referencia, e);
        }
    }

    private String extraerExtension(String nombreOriginal) {
        if (nombreOriginal == null){
            return "";
        }

        int indice = nombreOriginal.lastIndexOf('.');
        if (indice == -1){
            return "";
        }

        return nombreOriginal.substring(indice);
    }
}
