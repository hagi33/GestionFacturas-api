package com.fabio.GestionFacturas.infrastructure.adapter.in.web;

import com.fabio.GestionFacturas.domain.cliente.ClienteDuplicadoException;
import com.fabio.GestionFacturas.domain.cliente.ClienteInvalidoException;
import com.fabio.GestionFacturas.domain.cliente.ClienteNoEncontradoException;
import com.fabio.GestionFacturas.domain.gasto.GastoInvalidoException;
import com.fabio.GestionFacturas.domain.ingreso.IngresoInvalidoException;
import com.fabio.GestionFacturas.domain.ingreso.IngresoNoEncontradoException;
import com.fabio.GestionFacturas.domain.usuario.CredencialesInvalidadException;
import com.fabio.GestionFacturas.domain.usuario.EmailYaRegistradoException;
import com.fabio.GestionFacturas.infrastructure.adapter.out.storage.FileStorageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

/**
 * Translates domain/validation exceptions into HTTP responses — the one place that does this
 * mapping. The domain layer only ever throws plain exceptions and never knows about HTTP status
 * codes; {@code @RestControllerAdvice} intercepts them across every controller automatically.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GastoInvalidoException.class)
    public ResponseEntity<Map<String, String>> handleGastoInvalido(GastoInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(IngresoInvalidoException.class)
    public ResponseEntity<Map<String, String>> handleIngresoInvalido(IngresoInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(IngresoNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleIngresoNoEncontrado(IngresoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));  // 404
    }

    @ExceptionHandler(ClienteInvalidoException.class)
    public ResponseEntity<Map<String, String>> handleClienteInvalido(ClienteInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ClienteDuplicadoException.class)
    public ResponseEntity<Map<String, String>> handleClienteDuplicado(ClienteDuplicadoException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);      // 409
    }

    @ExceptionHandler(ClienteNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleClienteNoEncontrado(ClienteNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));  // 404
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores);
    }

    @ExceptionHandler(EmailYaRegistradoException.class)
    public ResponseEntity<Map<String, String>> handleEmailDuplicado(EmailYaRegistradoException ex){
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);      // 409
    }


    @ExceptionHandler(CredencialesInvalidadException.class)
    public ResponseEntity<Map<String , String>> handleCredencialesInvalidas(CredencialesInvalidadException ex){
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);  // 401
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<Map<String, String>> handleFileStorage(FileStorageException ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleTamanoMaximoExcedido(MaxUploadSizeExceededException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "El archivo supera el tamaño máximo permitido"));
    }
}


