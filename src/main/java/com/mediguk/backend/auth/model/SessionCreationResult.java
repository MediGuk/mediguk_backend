package com.mediguk.backend.auth.model;

import com.mediguk.backend.auth.entity.AuthSession;

public record SessionCreationResult(AuthSession session, String fingerprint, String fingerprintHash) {}
