package com.mediguk.backend.triage.dto;

public record PatternMatch(String patternName, Double similarityPercentage, String explanation) {}
