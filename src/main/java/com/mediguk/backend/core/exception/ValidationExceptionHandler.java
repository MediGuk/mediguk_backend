package com.mediguk.backend.core.exception;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class ValidationExceptionHandler {

  // Captura los fallos de @Valid en tus DTOs (400 Bad Request)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public Map<String, String> handleValidationException(MethodArgumentNotValidException ex) {
    log.warn("⚠️ Error de validación en los datos de entrada: {}", ex.getMessage());
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(
            error -> {
              errors.put(error.getField(), error.getDefaultMessage());
            });
    return errors;
  }

  // Para errores específicos de "No encontrado" (404)
  @ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  public Map<String, String> handleNotFound(Exception ex) {
    log.error("❌ Entidad no encontrada: {}", ex.getMessage());
    return Map.of("error", ex.getMessage());
  }

  // Para errores genéricos del servidor (500)
  @ExceptionHandler(RuntimeException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public Map<String, String> handleRuntimeException(RuntimeException ex) {
    // Logueamos el stacktrace completo para el desarrollador, pero enviamos mensaje corto al cliente
    log.error("💥 Error interno no controlado:", ex);
    
    Map<String, String> error = new HashMap<>();
    error.put("error", "Error interno del servidor");
    error.put("message", ex.getMessage()); // Mantenemos el message por ahora para facilitar debug al front
    return error;
  }
}
