package com.mediguk.backend.triage.dto.request;

import java.util.Map;

public record SymptomDTO(
    String key,
    String rawEvidence,
    Map<String, String> attributes
) {}
