package com.mediguk.backend.triage.model.specialty;

import java.time.LocalDateTime;

/////POLIMORFISMO/////
/**
 * Interfaz marcadora para todos los despieces médicos del Stage 1. Sello de identidad
 */
public interface SpecialtyDetails {
    String aiSummary();             // Obligatorio: ¿Qué vio la IA?
    String mainSymptom();           // Obligatorio: ¿Qué es lo más grave?
    String suspectedDiagnosis();    // Diagnosis que cree la ia pero solo como probable y NUNCA final...
    LocalDateTime createdAt();      // Obligatorio: ¿Cuándo se analizó?
}
