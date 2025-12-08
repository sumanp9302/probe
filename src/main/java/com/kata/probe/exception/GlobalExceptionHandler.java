package com.kata.probe.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(
                Map.of("error",
                        Map.of(
                                "code", "VALIDATION_ERROR",
                                "message", "Request validation failed"
                        )
                )
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleMalformedJson(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(
                Map.of("error",
                        Map.of(
                                "code", "VALIDATION_ERROR",
                                "message", "Malformed JSON request"
                        )
                )
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.unprocessableEntity().body(
                Map.of("error",
                        Map.of(
                                "code", "VALIDATION_ERROR",
                                "message", ex.getMessage()
                        )
                )
        );
    }
}