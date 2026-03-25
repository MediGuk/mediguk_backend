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

  // returns payload/claims of JWT: userID, claim (sessionToken), issuedAt, expiration
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

  // 🚨 Traductor Ultrarrápido Ed25519 (Curvas Elípticas) 🚨
  private PrivateKey getPrivateKey() {
    try {
        // Limpiamos la basura del .env como siempre
        String privateKeyPEM = jwtPrivateKey
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\n", "") // Quitamos tus \n literales
            .replaceAll("\\s", "");
        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
        
        // 🔮 AQUÍ ESTÁ LA NUEVA MAGIA MAGIA 🔮
        KeyFactory keyFactory = KeyFactory.getInstance("Ed25519");
        
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encoded));
    } catch (Exception e) {
        throw new RuntimeException("Error fatal leyendo la Llave Privada Ed25519", e);
    }
  }
  
    // 🚨 El traductor de texto a Llave PÚBLICA Ed25519 🚨
  private PublicKey getPublicKey() {
    try {
        // Limpiamos la basura del .env
        String publicKeyPEM = jwtPublicKey
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\\n", "") // Por si le has puesto saltos de línea literales
            .replaceAll("\\s", "");
        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
        
        // El motor de Curva Elíptica
        KeyFactory keyFactory = KeyFactory.getInstance("Ed25519");
        
        // Fíjate: Las Públicas siempre usan X509
        return keyFactory.generatePublic(new X509EncodedKeySpec(encoded));
    } catch (Exception e) {
        throw new RuntimeException("Error fatal leyendo la Llave Pública Ed25519", e);
    }
  }
}
