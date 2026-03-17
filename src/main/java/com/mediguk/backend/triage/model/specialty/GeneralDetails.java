package com.mediguk.backend.triage.model.specialty;

import java.time.LocalDateTime;

public record GeneralDetails(
    String aiSummary,
    String mainSymptom,
    String suspectedDiagnosis,
    LocalDateTime createdAt,
    // --- Específico de General ---
    String systemAffected,   // ¿Digestivo? ¿Neurológico? ¿Cardiovascular?
    String vitalSignsStatus, // ¿Parece estable o inestable?
    String generalState,     // Malestar general, astenia, mareo...
    boolean requiresUrgentLab // ¿Necesita analítica ya?
) implements SpecialtyDetails {}
