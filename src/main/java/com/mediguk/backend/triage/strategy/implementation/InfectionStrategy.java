package com.mediguk.backend.triage.strategy.implementation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediguk.backend.triage.dto.request.TriageRequestFromClient;
import com.mediguk.backend.triage.entity.TriageCase;
import com.mediguk.backend.triage.entity.TriageStatus;
import com.mediguk.backend.triage.exception.CategoryMismatchException;
import com.mediguk.backend.triage.exception.MedicalDataIncompleteException;
import com.mediguk.backend.triage.model.StageOneResult;
import com.mediguk.backend.triage.model.specialty.InfectionDetails;
import com.mediguk.backend.triage.service.AIService;
import com.mediguk.backend.triage.strategy.TriageStrategy;
import com.mediguk.backend.triage.util.RecordInspector;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class InfectionStrategy implements TriageStrategy {

    @Autowired
    private AIService aiService;
    @Autowired
    private ObjectMapper mapper;

    @Override
    public String getCategory() {
        return "INFECCION";
    }

    @Override
    public void conductStage1(TriageCase entity, TriageRequestFromClient request) {
        List<String> commonFields = RecordInspector.getFields(StageOneResult.class);
        List<String> specificFields = RecordInspector.getFields(InfectionDetails.class);

        String prompt = String.format(
            "Eres Infectólogo. Analiza: '%s'. " +
            "Devuelve un JSON con estos campos obligatorios: %s " +
            "Y dentro del objeto 'specialtyDetails', estos campos específicos: %s" +
            "IMPORTANTE: Para los campos booleanos  usa exclusivamente los valores JSON true o false. No añadas texto descriptivo.",
            request.rawInput(), commonFields, specificFields
        );

        StageOneResult aiResult = aiService.callVLM(request.imageUrl(), prompt);

        if (!aiResult.isCategoryValid()) {
            throw new CategoryMismatchException("INFECCION", aiResult.confirmedCategory());
        }

        try {
            InfectionDetails details = mapper.convertValue(
                aiResult.specialtyDetails(), 
                InfectionDetails.class
            );
            entity.getMedicalData().put("specialtyDetails", details);
            entity.setStatus(TriageStatus.ST1_SPECIALIST_ACCEPTED);
        } catch (Exception e) {
            log.error("La IA se inventó los campos de Infección: {}", e.getMessage());
            throw new MedicalDataIncompleteException("Contrato de Infección roto por la IA.");
        }
    }

    @Override public void conductStage2(TriageCase entity) {}
    @Override public void conductStage3(TriageCase entity) {}
}
