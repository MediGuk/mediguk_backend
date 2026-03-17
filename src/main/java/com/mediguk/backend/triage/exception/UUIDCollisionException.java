package com.mediguk.backend.triage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * EL ESCUDO FINAL: 
 * Se lanza si un UUID de Go choca con un paciente distinto en nuestra DB.
 */
@ResponseStatus(HttpStatus.CONFLICT) // Esto devuelve un 409 a Go
public class UUIDCollisionException extends RuntimeException {
    public UUIDCollisionException(String message) {
        super(message);
    }
}
