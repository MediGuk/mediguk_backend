package com.mediguk.backend.triage.entity;

import jakarta.persistence.*;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "triage_analyses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TriageAnalysis {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "case_id", nullable = false)
  private TriageCase triageCase; // Relación con el padre (DermatologyCase, etc.)

  // --- EL VEREDICTO (STAGE 3) ---
  private Integer finalUrgency;

  @Column(columnDefinition = "TEXT")
  private String diagnosisSuggestion;

  @Column(columnDefinition = "TEXT")
  private String clinicalReasoning; // Tu correlationNote

  @Column(columnDefinition = "TEXT")
  private String finalRecommendation;

  private Integer confidenceLevel; // 0-100 (La pelea Stage 1 vs Stage 2)

  // --- METADATOS DE DERIVACIÓN ---
  private String assignedSpecialty;
  private String healthcareLevel;
  private Boolean unknownFactor; // Anti-Hallucination: true si no hubo matches claros

  // --- EVIDENCIAS (STAGE 2) ---
  // Guardamos los puntos del historial que la IA consideró clave
  @ElementCollection private List<String> highlightedHistoryPoints;

  // Los patrones encontrados en la Vector DB (Stage 2)
  // Usamos un String TEXT para guardar el JSON del array y no complicar tablas
  @Column(columnDefinition = "TEXT")
  private String matchedPatternsJson;
}
