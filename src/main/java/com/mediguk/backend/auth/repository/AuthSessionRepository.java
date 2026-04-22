package com.mediguk.backend.auth.repository;

import com.mediguk.backend.auth.entity.AuthSession;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthSessionRepository extends JpaRepository<AuthSession, Long> {

  Optional<AuthSession> findBySessionTokenAndRevokedFalse(String sessionToken);

  Optional<AuthSession> findByRefreshTokenAndRevokedFalse(String refreshToken);
}
