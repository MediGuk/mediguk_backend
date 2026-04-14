package com.mediguk.backend.triage.dto.request;

public record TranscriptEntryDTO(
    String question, // La pregunta de la IA (o "INITIAL" en el primer turno)
    String answer    // La respuesta literal del paciente
) {}
