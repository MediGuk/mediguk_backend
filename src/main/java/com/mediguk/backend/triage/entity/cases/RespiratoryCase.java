package com.mediguk.backend.triage.entity.cases;

import com.mediguk.backend.triage.entity.TriageCase;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "respiratory_cases")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class RespiratoryCase extends TriageCase {
  private String coughType; // Seca, productiva, con sangre
  private Boolean dyspnea; // ¿Falta de aire? (Urgencia clave)
  private Double oxygenSaturation; // Si el paciente lo sabe (Stage 1 lo busca)
  private Boolean chestPain; // Dolor al respirar
  private String expectorationColor; // Verde, amarillo, transparente
}
