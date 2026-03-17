package com.mediguk.backend.triage.model.specialty;

import java.time.LocalDateTime;

public record MusculoSkeletalDetails(
    String aiSummary,
    String mainSymptom,
    String suspectedDiagnosis,
    LocalDateTime createdAt,
    // --- Específico de Trauma/Músculo ---
    String painLocation,     // ¿Hombro? ¿Rodilla? ¿Espalda?
    boolean mobilityLoss,    // ¿Puede moverlo o está bloqueado?
    boolean inflammation,    // ¿Está hinchado?
    String traumaHistory     // ¿Fue una caída? ¿Un mal gesto?
) implements SpecialtyDetails {}
