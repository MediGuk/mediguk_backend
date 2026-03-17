package com.mediguk.backend.triage.model.specialty;

import java.time.LocalDateTime;

public record DermatologyDetails(
    String aiSummary,
    String mainSymptom,
    String suspectedDiagnosis,
    LocalDateTime createdAt,
    // --- Lo específico de Derma ---
    boolean itching,
    String lesionColor,
    String texture,
    String evolution
) implements SpecialtyDetails {}
