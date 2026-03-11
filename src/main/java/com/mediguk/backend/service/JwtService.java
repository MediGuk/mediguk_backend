package com.mediguk.backend.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  @Value("${jwt.secret}")
  private String jwtSecret;

  public String generateToken(UUID userId, String sessionToken) {

    Date now = new Date();
    Date expiry = new Date(now.getTime() + 1000 * 60 * 60); // 1h

    return Jwts.builder() // return a token
        .subject(userId.toString()) // who is the user
        .claim("sessionToken", sessionToken)
        .issuedAt(now)
        .expiration(expiry)
        .signWith(getKey()) // sign it with the secret key
        .compact();
  }

  private Key getKey() {
    return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
  }
}
