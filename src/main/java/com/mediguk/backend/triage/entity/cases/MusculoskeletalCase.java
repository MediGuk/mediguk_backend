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
@Table(name = "musculoskeletal_cases")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class MusculoskeletalCase extends TriageCase {

  private String painLocation; // Lumbar, Cervical, Rodilla, etc.

  private Integer painIntensity; // Escala 1-10

  private Boolean mobilityRestriction; // ¿Puede caminar o mover el miembro?

  private Boolean traumaOrigin; // ¿Hubo caída o golpe directo?

  @Column(columnDefinition = "TEXT")
  private String injuryMechanism; // Ejemplo: "Giro brusco al cargar peso"

  private Boolean inflammation; // Hinchazón visible o calor en la zona
}
