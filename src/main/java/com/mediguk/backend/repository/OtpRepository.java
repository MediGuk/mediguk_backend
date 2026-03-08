package com.mediguk.backend.repository;

import com.mediguk.backend.entity.Otp;
import com.mediguk.backend.entity.User;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface OtpRepository extends JpaRepository<Otp, UUID> {

  Optional<Otp> findTopByUserOrderByExpiresAtDesc(User user);

  // Find Otp most recent one by user and not used and not expired
  @Query(
      """
      SELECT o
      FROM Otp o
      WHERE o.user = :user
      AND o.used = false
      AND o.expiresAt > :now
      ORDER BY o.expiresAt DESC
  """)
  Optional<Otp> findActiveOtp(User user, LocalDateTime now);

  // Convert before all Otp to used=true
  @Modifying
  @Query("UPDATE Otp o SET o.used = true WHERE o.user = :user")
  void invalidateOtpsForUser(User user);

  @Query(
      """
      SELECT SUM(o.attempts)
      FROM Otp o
      WHERE o.user = :user
      AND o.createdAt > :since
      """)
  Integer countRecentAttempts(User user, LocalDateTime since);

  @Modifying
  @Query("DELETE FROM Otp o WHERE o.expiresAt < :now")
  void deleteExpiredOtps(LocalDateTime now);
}
