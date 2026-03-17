package com.mediguk.backend.triage.dto.response;
// Respuesta final del analysis completo del triage

import com.mediguk.backend.triage.entity.TriageCase;
import com.mediguk.backend.triage.model.StageThreeResult;

public record FinalResponseToSanitary(
    // 1. EL BLOQUE DE HECHOS (Stage 0 y 1)
    // Aquí viaja el DermatologyCase completo con su ImageURL, historial, anatomSite, etc.
    TriageCase medicalData,

    // 2. EL BLOQUE DE INTELIGENCIA (Stage 2 y 3)
    // Aquí viaja el razonamiento, los PatternMatches y la recomendación
    StageThreeResult aiAnalysis) {}
