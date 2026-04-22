package com.mediguk.backend.auth.service;

import io.jsonwebtoken.Claims;
import java.util.UUID;

public interface JwtService {

    String generateToken(UUID userId, String sessionToken, String fingerprintHash);

    Claims parseToken(String token);

    long getExpirationTime();
}
