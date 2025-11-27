
package com.kata.probe.api.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> handleValidation(MethodArgumentNotValidException ex){
        return ResponseEntity.badRequest().body(Map.of(
                "error", Map.of("code", "VALIDATION_ERROR", "message", "Payload validation failed")
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,Object>> handleDomain(IllegalArgumentException ex){
        return ResponseEntity.unprocessableEntity().body(Map.of(
                "error", Map.of("code", "VALIDATION_ERROR", "message", ex.getMessage())
        ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String,Object>> handleMalformed(HttpMessageNotReadableException ex){
        // TDD GREEN: exact message required by the test
        return ResponseEntity.badRequest().body(Map.of(
                "error", Map.of("code", "VALIDATION_ERROR", "message", "Malformed JSON request")
        ));
    }
}
