package com.mediguk.backend.triage.dto.request;

import java.util.Map;

public record TriageRequest(
    Long patientId,
    String category, // "DERMATOLOGIA", "RESPIRATORIO", etc.
    String rawInput, // Lo que salió de Whisper
    String imageUrl, // La URL de S3 que mandó Go
    Map<String, Object> extraData // <-- ¡LA CLAVE! Aquí viene el "anatomSite", "fever", etc.
    ) {}
