package com.mediguk.backend.triage.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "triage_cases")
// @Inheritance(strategy = InheritanceType.JOINED) //QUITALO .... VAMOS A UTLIZAR COMPOSICON Y NO INHERITANCE
@Data
@NoArgsConstructor
@AllArgsConstructor
// @SuperBuilder // Ya no necesitas superBuilder si no es herencia
public class TriageCase { //abstract no ?? but why abstract ??? exlica lentamente broooo

  @Id
  // @GeneratedValue(strategy = GenerationType.IDENTITY) Ya lo genera Go
  private UUID id; // lo crea Go y asi tenemos trazabilidad

  @Column(name = "patient_id", nullable = false)
  private String patientId;

  // --- DATOS DE ENTRADA (STAGE 0) ----------------------------------------------------------------
  @Column(columnDefinition = "TEXT")
  private String rawPatientInput; //mensaje inicial + preguntas
  private String optimizedImageUrl;
  private String category; //Go te lo manda preguntado a client + mini vlm/llm Y stage1 si da error lo va y cambia

  // --- DESCRIPCIÓN MÉDICA (STAGE 1) --------------------------------------------------------------
  @ElementCollection
  // @CollectionTable(name = "case_history_points", joinColumns = @JoinColumn(name = "case_id"))
  // @Column(name = "point", columnDefinition = "TEXT")
  private List<String> cleanedMedicalHistory;
  private String cleanedPatientInput; // ordena y lo limpia todo el raw input que le manda el paciente

  // --- MOCHILA MEDICA (CORAZON) (Composicion flexible en vez de strict herencia) RECORDS Y STRATEGIES
  @JdbcTypeCode(SqlTypes.JSON) // Hibernate JSONB
  @Column(name = "medical_data", columnDefinition = "jsonb")
  // @Builder.Default
  private Map<String, Object> medicalData = new HashMap<>(); //Composicion para no tener herecnia suber largo... tenemos Records

  @Enumerated(EnumType.STRING) // Par qeu en db lo lea como texto
  @Column(name = "status")
  private TriageStatus status = TriageStatus.CREATED; //Maquina de estados

  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() { this.createdAt = LocalDateTime.now();}

  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public TriageCase(UUID id, String rawPatientInput, TriageStatus status) {
    this.id = id;
    this.rawPatientInput = rawPatientInput;
    this.status = status;
    this.medicalData = new HashMap<>(); // La mochila nace vacía pero lista  (para qeu no sea null)
  }

}

// {
//   "stage1": { "category": "DERMA", "details": { "pica": true, "color": "rojo" } },
//   "stage2": { "risk": 0.85, "matches": [...] },
//   "stage3": { "urgency": 7, "diagnosis": "Queratosis" }
// } POLIMORFISMO
