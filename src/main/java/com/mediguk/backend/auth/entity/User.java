package com.mediguk.backend.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

  @Id @GeneratedValue private UUID id;

  @Column(unique = true, nullable = false)
  private String documentNumber; // DNI, NIE , Passport

  private String fullName;

  private String email;

  private String phoneNumber;
}
