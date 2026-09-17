package com.fabio.GestionFacturas.infrastructure.adapter.out.storage;

/** Thrown by {@link LocalStorageAdapter} on I/O failure. Mapped by GlobalExceptionHandler to HTTP 500. */
public class FileStorageException extends RuntimeException {

    public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
