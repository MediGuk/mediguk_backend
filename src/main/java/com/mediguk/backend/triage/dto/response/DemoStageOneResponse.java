package com.mediguk.backend.triage.dto.response;

import com.mediguk.backend.triage.entity.TriageStatus;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record DemoStageOneResponse (
    UUID id,
    String category,
    TriageStatus status,
    List<Map<String, String>> fullTranscript,
    // Aquí mandamos la "mochila" de Stage 1 filtrada
    Map<String, Object> stage1Details
 ) {}
