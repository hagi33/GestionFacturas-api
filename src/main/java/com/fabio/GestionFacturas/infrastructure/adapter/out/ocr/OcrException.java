package com.fabio.GestionFacturas.infrastructure.adapter.out.ocr;

/** Thrown by {@link TesseractOcrAdapter} on unreadable/unsupported input or engine failure. Not mapped by GlobalExceptionHandler — falls through to a default 500. */
public class OcrException extends RuntimeException {

    public OcrException(String message) {
        super(message);
    }

    public OcrException(String message, Throwable cause) {
        super(message, cause);
    }
}
