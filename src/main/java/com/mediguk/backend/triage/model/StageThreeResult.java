// Hoy en dia se tulzia Record que getter y setter los hace jackson autmoaticamente ya y lombok no
// ahce flata que record ya trae @data y @allargsConstructor
package com.mediguk.backend.triage.model;

import jakarta.validation.constraints.*;
import java.util.List;
import lombok.Builder;

@Builder
public record StageThreeResult(
    @NotNull @Min(0) @Max(10) Integer finalUrgency, // 0-10 (De "tranquilo" a "corre al hospital")
    @NotBlank String diagnosisSuggestion,
    
    // Lo que el Juez destaca del pasado del paciente
    List<String> highlightedHistoryPoints, 
    
    // --- CONEXIÓN CON STAGE 2 ---
    // En lugar de PatternMatch genérico, usamos el resultado completo del Stage 2
    StageTwoResult evidenceUsed, 
    
    @NotBlank String correlationNote, // El "Por qué": Stage 1 dice X y Stage 2 dice Y
    @NotBlank String recommendation,  // "¿Qué tiene que hacer el paciente ahora?"
    
    @Min(0) @Max(100) Integer confidenceLevel, // Coincidencia entre IA (S1) y Ciencia (S2)
    @NotBlank String assignedSpecialty, 
    @NotBlank String healthcareLevel, // Cabecera, Especialista, Urgencias...
    
    boolean unknownFactor // Si el Juez dice: "No entiendo nada, que lo vea un humano YA"
) {}
