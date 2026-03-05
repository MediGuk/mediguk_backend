package com.mediguk.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.util.UUID;
import lombok.AllArgsConstructor; // Generate instancies manually
import lombok.Data; // Generates getters, setters, constructor and equals/hashCode automatically
import lombok.NoArgsConstructor;

@Entity
@Data // get*, set*, constructor, equals() y hashCode()
@NoArgsConstructor
@AllArgsConstructor
public class User {

  @Id @GeneratedValue private UUID id;

  private String email;

  private String password;

  private String phoneNumber;
}
