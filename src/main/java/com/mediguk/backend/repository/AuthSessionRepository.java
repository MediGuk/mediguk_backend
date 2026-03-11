package com.mediguk.backend.repository;

import com.mediguk.backend.entity.AuthSession;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthSessionRepository extends JpaRepository<AuthSession, Long> {

  Optional<AuthSession> findByIdAndRevokedFalse(Long id);
}
