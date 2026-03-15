package com.mediguk.backend.triage.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List; // Te faltaba este import, bro
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "triage_cases")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class TriageCase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "patient_id", nullable = false)
  private Long patientId;

  // --- DATOS DE ENTRADA (STAGE 0) ---
  @Column(columnDefinition = "TEXT")
  private String rawPatientInput;

  private String optimizedImageUrl;

  // --- DESCRIPCIÓN MÉDICA (STAGE 1) ---
  @ElementCollection
  @CollectionTable(name = "case_history_points", joinColumns = @JoinColumn(name = "case_id"))
  @Column(name = "point", columnDefinition = "TEXT")
  private List<String> cleanedMedicalHistory;

  private String
      probableCategory; // "DERMATOLOGIA", "RESPIRATORIO"... // los hijos ahora mismo son muy
  // basicos

  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
  }
}
