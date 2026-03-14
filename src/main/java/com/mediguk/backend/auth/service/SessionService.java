package com.mediguk.backend.auth.service;

import com.mediguk.backend.auth.entity.AuthSession;
import com.mediguk.backend.auth.entity.User;
import com.mediguk.backend.auth.model.SessionCreationResult;
import com.mediguk.backend.auth.repository.AuthSessionRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SessionService {

  private final AuthSessionRepository authSessionRepository;
  private final PasswordEncoder passwordEncoder;

  public SessionCreationResult createSession(User user, HttpServletRequest request) {

    // FUNDAMENTAL
    String fingerprintRaw = generateFingerprint();
    String fingerprintHash = passwordEncoder.encode(fingerprintRaw);

    String sessionToken = UUID.randomUUID().toString(); // inside JWT payload/Claims
    String refreshToken = UUID.randomUUID().toString(); // by HTTP endpoint

    AuthSession session =
        AuthSession.builder()
            .user(user)
            .sessionToken(sessionToken) // inside JWT of 1h
            .refreshToken(
                refreshToken) // rotative refresh cookie of 30 days but each time change of value
            .fingerprintHash(fingerprintHash)
            .deviceId(request.getHeader("X-Device-Id"))
            .ip(request.getRemoteAddr())
            .userAgent(request.getHeader("User-Agent"))
            .createdAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusDays(30)) // session duration of 30 days
            .build();

    AuthSession savedSession = authSessionRepository.save(session);
    return new SessionCreationResult(savedSession, fingerprintRaw);
  }

  private String generateFingerprint() {

    SecureRandom secureRandom = new SecureRandom();

    byte[] randomBytes = new byte[32];

    secureRandom.nextBytes(randomBytes);

    return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
  }

  @Transactional
  public void revokeSession(String sessionToken) {
    authSessionRepository
        .findBySessionTokenAndRevokedFalse(sessionToken)
        .ifPresent(
            session -> {
              session.setRevoked(true);
            });
  }

  // Search in AuthSession table a sesson with specific refreshToken & no revoked
  @Transactional
  public AuthSession getSessionByRefreshToken(String refreshToken) {
    return authSessionRepository
        .findByRefreshTokenAndRevokedFalse(refreshToken)
        .orElseThrow(() -> new RuntimeException("Refresh token no válido o sesión revocada"));
  }

  @Transactional
  public String rotateRefreshToken(AuthSession session) {
    String newRefreshToken = UUID.randomUUID().toString();
    session.setRefreshToken(newRefreshToken);
    authSessionRepository.save(session);
    return newRefreshToken;
  }

  @Transactional
  public AuthSession getValidSession(String refreshToken, String fingerprintRaw) {

    // 1. Find session by refreshToken & revoked=false
    AuthSession session =
        authSessionRepository
            .findByRefreshTokenAndRevokedFalse(refreshToken)
            .orElseThrow(() -> new RuntimeException("Refresh token no válido o sesión revocada"));

    // 2. Validate session by expiration
    if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
      session.setRevoked(true); // Kill it if expired
      throw new RuntimeException("Sesión expirada");
    }

    // 3. Validate session by rawFingerprint cookie
    if (!passwordEncoder.matches(fingerprintRaw, session.getFingerprintHash())) {
      session.setRevoked(true); // Kill it if fingerprint doesn't match
      throw new RuntimeException("Seguridad: Huella no válida");
    }

    return session;
  }
}
