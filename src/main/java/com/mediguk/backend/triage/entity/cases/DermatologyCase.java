package com.mediguk.backend.triage.entity.cases;

import com.mediguk.backend.triage.entity.TriageCase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "dermatology_cases")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class DermatologyCase extends TriageCase {

  // --- DESCRIPCIÓN (Stage 1) ---
  @Column(columnDefinition = "TEXT")
  private String visualDescription; // Relato clínico

  @Column(columnDefinition = "TEXT")
  private String vlmVisualPattern; // Datos estructurados (Asimetría, bordes, etc.)

  // --- CAMPOS ESPECÍFICOS DE DERMA ---
  private String anatomSite; // Ejemplo: "Espalda", "Brazo izquierdo"
  private String isicClassification; // Clasificación estándar (benign, malignant, etc.)
  private Boolean lesionBleeding; // ¿Sangra? (Red Flag para Stage 3)

  private String suggestedDiagnosis; // Lo que cree el Stage 1 antes de ver la DB
}
