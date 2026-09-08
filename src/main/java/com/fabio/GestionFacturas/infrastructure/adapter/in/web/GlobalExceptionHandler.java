package com.fabio.GestionFacturas.infrastructure.adapter.in.web;

import com.fabio.GestionFacturas.domain.gasto.GastoInvalidoException;
import com.fabio.GestionFacturas.domain.usuario.CredencialesInvalidadException;
import com.fabio.GestionFacturas.domain.usuario.EmailYaRegistradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GastoInvalidoException.class)
    public ResponseEntity<Map<String, String>> handleGastoInvalido(GastoInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
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
}


