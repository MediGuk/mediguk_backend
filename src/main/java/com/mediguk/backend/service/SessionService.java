package com.mediguk.backend.service;

import com.mediguk.backend.entity.AuthSession;
import com.mediguk.backend.entity.User;
import com.mediguk.backend.model.SessionCreationResult;
import com.mediguk.backend.repository.AuthSessionRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionService {

  private final AuthSessionRepository authSessionRepository;
  private final PasswordEncoder passwordEncoder;

  public SessionCreationResult createSession(User user, HttpServletRequest request) {

    // FUNDAMENTAL
    String fingerprint = generateFingerprint();
    String fingerprintHash = passwordEncoder.encode(fingerprint);

    String sessionToken = UUID.randomUUID().toString();

    AuthSession session =
        AuthSession.builder()
            .user(user)
            .sessionToken(sessionToken)
            .fingerprintHash(fingerprintHash)
            .deviceId(request.getHeader("X-Device-Id"))
            .ip(request.getRemoteAddr())
            .userAgent(request.getHeader("User-Agent"))
            .createdAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusDays(30))
            .build();

    AuthSession savedSession = authSessionRepository.save(session);
    return new SessionCreationResult(savedSession, fingerprint);
  }

  // método privado para generar fingerprint
  private String generateFingerprint() {

    SecureRandom secureRandom = new SecureRandom();

    byte[] randomBytes = new byte[32];

    secureRandom.nextBytes(randomBytes);

    return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
  }
}
