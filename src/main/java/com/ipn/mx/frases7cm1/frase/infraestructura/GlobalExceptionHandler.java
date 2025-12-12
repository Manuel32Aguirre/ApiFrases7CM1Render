package com.ipn.mx.frases7cm1.frase.infraestructura;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para la API
 * Captura todas las excepciones y las convierte en respuestas HTTP apropiadas
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja errores de validación (cuando @Valid falla)
     * Retorna 400 Bad Request con los mensajes de error de cada campo
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

        // Extrae todos los errores de validación
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Errores de validación");
        response.put("errores", errors);

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Maneja cualquier excepción no controlada
     * Retorna 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "Error interno del servidor");
        response.put("mensaje", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Maneja errores cuando no hay frases para seleccionar aleatoriamente
     * Retorna 404 Not Found
     */
    @ExceptionHandler(IndexOutOfBoundsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleNoFrasesDisponibles(IndexOutOfBoundsException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", "No hay frases disponibles");
        response.put("mensaje", "No existen frases en el sistema. Por favor, agregue al menos una frase.");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}

