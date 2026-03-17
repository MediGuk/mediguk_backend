package com.mediguk.backend.triage.dto.response;

import com.mediguk.backend.triage.entity.TriageStatus;
import java.util.Map;
import java.util.UUID;

public record DemoStageOneResponse (
    UUID id,
    String patientId,
    String category,
    TriageStatus status,
    //FiltrespsaitientInput
    //algo mas ???
    // Aquí mandamos la "mochila" de Stage 1 filtrada
    Map<String, Object> stage1Details
 ) {}
