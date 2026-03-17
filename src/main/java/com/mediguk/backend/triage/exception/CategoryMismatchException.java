package com.mediguk.backend.triage.exception;

import lombok.Getter;

/**
 * EXCEPCIÓN DE VOLANTAZO:
 * Se lanza cuando el especialista (Stage 1) detecta que el caso 
 * no pertenece a su categoría y sugiere una nueva.
 */
@Getter
public class CategoryMismatchException extends RuntimeException {

    private final String oldCategory;
    private final String newCategory;
    private final String reason;

    public CategoryMismatchException(String oldCategory, String newCategory, String reason) {
        super(String.format("Conflicto de categoría: Se esperaba %s pero la IA sugiere %s. Motivo: %s", 
              oldCategory, newCategory, reason));
        this.oldCategory = oldCategory;
        this.newCategory = newCategory;
        this.reason = reason;
    }

    // Constructor simplificado
    public CategoryMismatchException(String oldCategory, String newCategory) {
        this(oldCategory, newCategory, "La IA detectó una especialidad diferente durante el análisis.");
    }
}
