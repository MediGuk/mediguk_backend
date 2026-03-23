package com.mediguk.backend.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  @Value("${app.security.jwt.secret-key}")
  private String jwtSecret;

  @Value("${mediguk.jwt.expiration}")
  private long jwtExpiration;

  public String generateToken(UUID userId, String sessionToken) {

    Date now = new Date();
    Date expiry = new Date(now.getTime() + jwtExpiration); // + 1h

    return Jwts.builder() // return a token
        .subject(userId.toString()) // who is the user
        .claim("sessionToken", sessionToken) // token of the active session
        .issuedAt(now)
        .expiration(expiry)
        .signWith(getKey()) // sign it with the secret key
        .compact();
  }

  private SecretKey getKey() {
    return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
  }

  // returns payload/claims of JWT: userID, claim (sessionToken), issuedAt, expiration
  public Claims parseToken(String token) {

    return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
  }

  public long getExpirationTime() {
    return jwtExpiration;
  }
}
