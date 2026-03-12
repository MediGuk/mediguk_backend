package com.mediguk.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "auth_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthSession {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String sessionToken; // JWT has it in claims

  @Column(unique = true, nullable = false)
  private String refreshToken;

  @ManyToOne(optional = false)
  private User user;

  private String deviceId;

  private String ip;

  private String userAgent;

  private String fingerprintHash;

  @Builder.Default private boolean revoked = false; // session active or revoked

  private LocalDateTime createdAt;

  private LocalDateTime expiresAt;
}
