package com.mediguk.backend.triage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Cuando la IA no cumple el contrato de los Records médicos.
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class MedicalDataIncompleteException extends RuntimeException {
    public MedicalDataIncompleteException(String message) {
        super(message);
    }
}
