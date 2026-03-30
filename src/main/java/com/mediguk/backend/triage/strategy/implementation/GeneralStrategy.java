package com.mediguk.backend.triage.strategy.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediguk.backend.triage.dto.request.TriageRequest;
import com.mediguk.backend.triage.entity.TriageCase;
import com.mediguk.backend.triage.entity.TriageStatus;
import com.mediguk.backend.triage.exception.MedicalDataIncompleteException;
import com.mediguk.backend.triage.model.StageOneResult;
import com.mediguk.backend.triage.model.specialty.GeneralDetails;
import com.mediguk.backend.triage.service.AIService;
import com.mediguk.backend.triage.strategy.TriageStrategy;
import com.mediguk.backend.triage.util.RecordInspector;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GeneralStrategy implements TriageStrategy {

  @Autowired private AIService aiService;
  @Autowired private ObjectMapper mapper;

  @Override
  public String getCategory() {
    return "GENERAL";
  }

  @Override
  public void conductStage1(TriageCase entity, TriageRequest request) {
    List<String> commonFields = RecordInspector.getFields(StageOneResult.class);
    List<String> specificFields = RecordInspector.getFields(GeneralDetails.class);

    String prompt =
        String.format(
            "Eres Médico de Medicina General. Analiza: '%s'. "
                + "Devuelve un JSON con estos campos obligatorios: %s "
                + "Y dentro del objeto 'specialtyDetails', estos campos específicos: %s"
                + "IMPORTANTE: Para los campos booleanos  usa exclusivamente los valores JSON true o false. No añadas texto descriptivo.",
            request.resumeClinic(), commonFields, specificFields);

    StageOneResult aiResult = aiService.callVLM(request.imageUrl(), prompt);

    // En General no solemos lanzar CategoryMismatchException porque es el fallback.
    try {
      GeneralDetails details =
          mapper.convertValue(aiResult.specialtyDetails(), GeneralDetails.class);
      entity.getMedicalData().put("specialtyDetails", details);
      entity.setStatus(TriageStatus.ST1_SPECIALIST_ACCEPTED);
    } catch (Exception e) {
      log.error("La IA se inventó los campos de General: {}", e.getMessage());
      throw new MedicalDataIncompleteException("Contrato de Medicina General roto.");
    }
  }

  @Override
  public void conductStage2(TriageCase entity) {}

  @Override
  public void conductStage3(TriageCase entity) {}
}
