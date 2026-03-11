package com.mediguk.backend.model;

import com.mediguk.backend.entity.AuthSession;

public record SessionCreationResult(AuthSession session, String fingerprint) {}
