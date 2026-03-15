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
@Table(name = "administrative_cases")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class AdministrativeCase extends TriageCase {

  private String requestType; // "RECETA", "BAJA_MEDICA", "INFORME", "RESULTADOS"

  @Column(columnDefinition = "TEXT")
  private String specificRequirement; // Nombre del medicamento o motivo de la baja

  private Boolean isUrgentRenewal; // Para recetas que se acaban hoy mismo

  private String linkedSpecialty; // Por si pide cita para un especialista concreto
}
