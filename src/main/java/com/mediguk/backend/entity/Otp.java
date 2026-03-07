package com.mediguk.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "otps")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Otp {

  @Id @GeneratedValue // serial-primary key
  private UUID id;

  private String code;

  private LocalDateTime expiresAt;

  // JOIN with user
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;
}
