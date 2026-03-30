package com.mediguk.backend.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.KeyFactory;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  @Value("${app.security.jwt.private-key}")
  private String jwtPrivateKey;

  @Value("${app.security.jwt.public-key}")
  private String jwtPublicKey;

  @Value("${mediguk.jwt.expiration}")
  private long jwtExpiration;

  public String generateToken(UUID userId, String sessionToken, String fingerprintHash) {

    Date now = new Date();
    Date expiry = new Date(now.getTime() + jwtExpiration); // + 1h

    return Jwts.builder() // return a token
        .subject(userId.toString()) // who is the user
        .claim("sessionToken", sessionToken) // token of the active session
        .claim("fingerprintHash", fingerprintHash) // hash of fingerprint for Go microservice
        .issuedAt(now)
        .expiration(expiry)
        .signWith(getPrivateKey(), Jwts.SIG.EdDSA) // sign it with asymetric private key EdDSA
        .compact();
  }

  // returns payload/claims of JWT: userID, claim (sessionToken), issuedAt,
  // expiration
  public Claims parseToken(String token) {
    return Jwts.parser()
        .verifyWith(getPublicKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  public long getExpirationTime() {
    return jwtExpiration;
  }

    private PrivateKey getPrivateKey() {
        try {
            String step1 = jwtPrivateKey.replace("\\n", "\n");
            String step2 = step1.replace("-----BEGIN PRIVATE KEY-----", "");
            String step3 = step2.replace("-----END PRIVATE KEY-----", "");
            String step4 = step3.replaceAll("\\s", "").trim();

            int padding = step4.length() % 4;
            if (padding > 0) step4 += "=".repeat(4 - padding);

            byte[] encoded = Base64.getDecoder().decode(step4);
            KeyFactory keyFactory = KeyFactory.getInstance("Ed25519");
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encoded));
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            throw new RuntimeException("Error leyendo clave privada", e);
        }
    }

  // 🚨 El traductor de texto a Llave PÚBLICA Ed25519 🚨
  private PublicKey getPublicKey() {
      try {
              String step1 = jwtPublicKey.replace("\\n", "\n")  ;        // PRIMERO convierte \n literal a salto real
              String step2 = step1.replace("-----BEGIN PUBLIC KEY-----", "");
              String step3 = step2.replace("-----END PUBLIC KEY-----", "");
              String step4 = step3.replaceAll("\\s", "").trim();         // elimina todos los espacios/saltos reales

          int padding = step4.length() % 4;
          if (padding > 0) {
              step4 += "=".repeat(4 - padding);
          }

          byte[] encoded = Base64.getDecoder().decode(step4);
          KeyFactory keyFactory = KeyFactory.getInstance("Ed25519");
          return keyFactory.generatePublic(new X509EncodedKeySpec(encoded));
      } catch (Exception e) {
          System.err.println("❌ Error fatal leyendo la Llave Pública Ed25519: " + e.getMessage());
          throw new RuntimeException("Error fatal leyendo clave pública", e);
      }
  }
}
