package com.mediguk.backend.triage.model.specialty;

import java.time.LocalDateTime;

public record InfectionDetails(
    String aiSummary,
    String mainSymptom,
    String suspectedDiagnosis,
    LocalDateTime createdAt,
    // --- Específico de Infección ---
    double temperature,      // La fiebre es clave aquí
    boolean chills,          // Escalofríos
    String lymphNodesStatus, // Ganglios (inflamados/normales)
    String infectionFocus    // ¿Garganta? ¿Urinario? ¿Herida?
) implements SpecialtyDetails {}
