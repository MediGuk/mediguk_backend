package com.mediguk.backend.triage.model.specialty;

import java.time.LocalDateTime;

public record RespiratoryDetails(
    String aiSummary,
    String mainSymptom,
    String suspectedDiagnosis,
    LocalDateTime createdAt,
    // --- Lo específico de Respiratory ---
    double oxygenSaturation,
    boolean dryCough,
    boolean fever
) implements SpecialtyDetails {}
