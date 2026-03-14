package com.mediguk.backend.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor; // Generate instancies manually
import lombok.Data; // Generates getters, setters, constructor and equals/hashCode automatically
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data // get*, set*, constructor, equals() y hashCode()
@NoArgsConstructor
@AllArgsConstructor
public class User {

  @Id @GeneratedValue private UUID id;

  @Column(unique = true, nullable = false)
  private String documentNumber; // DNI, NIE , Passport

  private String fullName;

  private String email;

  private String phoneNumber;
}
