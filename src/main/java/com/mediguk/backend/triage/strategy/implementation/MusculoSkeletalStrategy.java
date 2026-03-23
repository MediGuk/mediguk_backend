package com.mediguk.backend.triage.strategy.implementation;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.mediguk.backend.triage.entity.TriageCase;
import com.mediguk.backend.triage.entity.TriageStatus;
import com.mediguk.backend.triage.dto.request.TriageRequest;
import com.mediguk.backend.triage.model.StageOneResult;
import com.mediguk.backend.triage.model.specialty.MusculoSkeletalDetails;
import com.mediguk.backend.triage.strategy.TriageStrategy;
import com.mediguk.backend.triage.util.RecordInspector;
import com.mediguk.backend.triage.service.AIService;
import com.mediguk.backend.triage.exception.CategoryMismatchException;
import com.mediguk.backend.triage.exception.MedicalDataIncompleteException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MusculoSkeletalStrategy implements TriageStrategy {

    @Autowired
    private AIService aiService;
    @Autowired
    private ObjectMapper mapper;

    @Override
    public String getCategory() {
        return "MUSCULO_ESQUELETICO";
    }

    @Override
    public void conductStage1(TriageCase entity, TriageRequest request) {
        List<String> commonFields = RecordInspector.getFields(StageOneResult.class);
        List<String> specificFields = RecordInspector.getFields(MusculoSkeletalDetails.class);

        String prompt = String.format(
            "Eres Traumatólogo. Analiza: '%s'. " +
            "Devuelve un JSON con estos campos obligatorios: %s " +
            "Y dentro del objeto 'specialtyDetails', estos campos específicos: %s" +
            "IMPORTANTE: Para los campos booleanos  usa exclusivamente los valores JSON true o false. No añadas texto descriptivo.",
            request.rawInput(), commonFields, specificFields
        );

        StageOneResult aiResult = aiService.callVLM(request.imageUrl(), prompt);

        if (!aiResult.isCategoryValid()) {
            throw new CategoryMismatchException("MUSCULO_ESQUELETICO", aiResult.confirmedCategory());
        }

        try {
            MusculoSkeletalDetails details = mapper.convertValue(
                aiResult.specialtyDetails(), 
                MusculoSkeletalDetails.class
            );
            entity.getMedicalData().put("specialtyDetails", details);
            entity.setStatus(TriageStatus.ST1_SPECIALIST_ACCEPTED);
        } catch (Exception e) {
            log.error("La IA se inventó los campos de Musculo-Esquelético: {}", e.getMessage());
            throw new MedicalDataIncompleteException("Contrato de Músculo roto por la IA.");
        }
    }

    @Override public void conductStage2(TriageCase entity) {}
    @Override public void conductStage3(TriageCase entity) {}
}
