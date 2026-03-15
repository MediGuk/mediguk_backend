package com.mediguk.backend.triage.entity.cases;

import com.mediguk.backend.triage.entity.TriageCase;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "infection_cases")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class GeneralInfectionCase extends TriageCase {
  private Double temperature;
  private String infectionSite; // "Urinario", "Digestivo", "Ocular"
  private Boolean vomitingOrNausea;
  private Integer symptomDurationDays;
}
