package com.fabio.GestionFacturas.infrastructure.adapter.out.storage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class LocalStorageAdapterTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("guarda un archivo y lo recupera con el mismo contenido")
    void guardaYRecuperaElMismoContenido(){
        LocalStorageAdapter adapter = new LocalStorageAdapter(tempDir.toString());
        byte[] contenido = "contenido de prueba".getBytes(StandardCharsets.UTF_8);

        String referencia = adapter.guardar(contenido, "factura.pdf", "application/pdf");
        byte[] recuperado = adapter.recuperar(referencia);

        assertThat(recuperado).isEqualTo(contenido);
        assertThat(referencia).endsWith(".pdf");
    }

    @Test
    @DisplayName("genera referencias distintas para archivos con el mismo nombre")
    void generaReferenciasDistintasParaNombresIguales(){
        LocalStorageAdapter adapter = new LocalStorageAdapter(tempDir.toString());
        byte[] contenido = "contenido".getBytes(StandardCharsets.UTF_8);

        String referencia1 = adapter.guardar(contenido, "factura.pdf", "application/pdf");
        String referencia2 = adapter.guardar(contenido, "factura.pdf", "application/pdf");

        assertThat(referencia1).isNotEqualTo(referencia2);
    }

    @Test
    @DisplayName("elimina el archivo guardado")
    void eliminaElArchivoGuardado(){
        LocalStorageAdapter adapter = new LocalStorageAdapter(tempDir.toString());
        byte[] contenido = "contenido de prueba".getBytes(StandardCharsets.UTF_8);
        String referencia = adapter.guardar(contenido, "factura.pdf", "application/pdf");

        adapter.eliminar(referencia);

        assertThatThrownBy(() -> adapter.recuperar(referencia))
                .isInstanceOf(FileStorageException.class);
    }

    @Test
    @DisplayName("lanza excepción al recuperar una referencia inexistente")
    void fallaAlRecuperarReferenciaInexistente(){
        LocalStorageAdapter adapter = new LocalStorageAdapter(tempDir.toString());

        assertThatThrownBy(() -> adapter.recuperar("no-existe.pdf"))
                .isInstanceOf(FileStorageException.class);
    }

    @Test
    @DisplayName("crea el directorio base si no existe")
    void creaDirectorioBaseSiNoExiste(){
        Path subdirectorio = tempDir.resolve("nested/storage");

        new LocalStorageAdapter(subdirectorio.toString());

        assertThat(Files.exists(subdirectorio)).isTrue();
    }
}
