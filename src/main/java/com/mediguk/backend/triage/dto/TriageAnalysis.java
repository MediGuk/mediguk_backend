// Hoy en dia se tulzia Record que getter y setter los hace jackson autmoaticamente ya y lombok no
// ahce flata que record ya trae @data y @allargsConstructor
package com.mediguk.backend.triage.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public record TriageAnalysis(
    @NotNull @Min(0) @Max(10) Integer finalUrgency,
    @NotBlank String diagnosisSuggestion,
    List<String> highlightedHistoryPoints,
    @NotEmpty List<PatternMatch> matchedPatterns,
    @NotBlank String correlationNote,
    @NotBlank String recommendation,
    @Min(0) @Max(100) Integer confidenceLevel, // ¿Cuánto coinciden Stage 1 y Stage 2?
    @NotBlank String assignedSpecialty, // Derma, Trauma, Respiratorio...
    @NotBlank String healthcareLevel, // Cabecera, Especialista, Urgencias hospitalarias...
    boolean unknownFactor // TRUE si no hay matches claros en Stage 2 (va directo a médico)
    ) {}
