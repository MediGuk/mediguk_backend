package com.mediguk.backend.auth.model;

// accessToken -> JWT
// Client has fingerprint as cookie and server has the same fingerprint but hashed
public record AuthResult(String jwtToken, String refreshToken, String fingerprintRaw) {}
