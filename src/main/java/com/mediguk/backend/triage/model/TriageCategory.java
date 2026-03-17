package com.mediguk.backend.triage.model;
// ENUM es mejor qeu const ?? POERUQE ?? HAZ MODIFICIACIOENS EN FCTORY Y ASI ....

public enum TriageCategory {
    DERMATOLOGIA,
    RESPIRATORIO,
    MUSCULO_ESQUELETICO,
    INFECCION,
    GENERAL;

    //ADMINISTRATIVO (future)

    // Método de seguridad para convertir String a Enum sin que explote
    public static TriageCategory fromString(String category) {
        try {
            return TriageCategory.valueOf(category.toUpperCase());
        } catch (Exception e) {
            return GENERAL; // Si Go manda basura, somos resilientes
        }
    }
}