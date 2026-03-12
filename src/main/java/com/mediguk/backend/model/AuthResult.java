package com.mediguk.backend.model;

// accessToken -> JWT
// Client has fingerprint as cookie and server has the same fingerprint but hashed
public record AuthResult(String accessToken, String fingerprint) {}
