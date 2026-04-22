package com.mediguk.backend.triage.strategy.impl;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper; // Transformar un JSON (o un Mapa de Java) en tus Records

// --- IMPORTS DEL DOMINIO MEDIGUK ---
import com.mediguk.backend.triage.entity.TriageCase;
import com.mediguk.backend.triage.entity.TriageStatus;
import com.mediguk.backend.triage.dto.request.TriageRequest;
import com.mediguk.backend.triage.model.StageOneResult;
import com.mediguk.backend.triage.model.specialty.DermatologyDetails;
import com.mediguk.backend.triage.strategy.TriageStrategy;
import com.mediguk.backend.triage.util.RecordInspector;

import lombok.extern.slf4j.Slf4j;

import com.mediguk.backend.triage.service.AIService; // El que habla con Gemini/GPT
import com.mediguk.backend.triage.exception.CategoryMismatchException;
import com.mediguk.backend.triage.exception.MedicalDataIncompleteException;

/**
 * ESTRATEGIA: DERMATOLOGÍA
 * Esta clase es el "cerebro" cuando el caso entra por la puerta de piel.
 */
@Slf4j
@Component
public class DermatologyStrategy implements TriageStrategy { //extends BaseTriageStrategy (polimorfismo + herencia) ?????
    ////////////////////////*********polimorfismo vs herencia *******************///////////////////////////////

    @Autowired
    private AIService aiService; // El motor de IA que ejecuta nuestros prompts

    // Inyectamos el mapper que Spring ya tiene creado para nosotros.
    @Autowired
    private ObjectMapper mapper;

    // FACTORY BUSCA AQUI
    @Override
    public String getCategory() {
        return "DERMATOLOGIA";
    }

    /**
     * STAGE 1: ANALISTA CUTÁNEO
     * Aquí lanzamos el prompt experto y sellamos la mochila JSONB.
     */
    @Override
    public void conductStage1(TriageCase entity, TriageRequest request) {

        // 1. GENERAMOS LA LISTA DE CAMPOS AUTOMÁTICAMENTE
        // Si mañana añades "size" al Record, esto lo pilla solo.
        List<String> commonFields = RecordInspector.getFields(StageOneResult.class);
        List<String> dermaFields = RecordInspector.getFields(DermatologyDetails.class);

        // 2. EL PROMPT SE CONSTRUYE SOLO. Y LA IMAGEN ?? Y EL HISTORIAL MEDICO ???? RawInput es solo texto broooo
        String prompt = String.format(
            "Eres Dermatólogo. Analiza: '%s'. " +
            "Devuelve un JSON con estos campos obligatorios: %s " +
            "Y dentro del objeto 'specialtyDetails', estos campos específicos: %s" +
            "IMPORTANTE: Para los campos booleanos  usa exclusivamente los valores JSON true o false. No añadas texto descriptivo.",
            request.resumeClinic(), commonFields, dermaFields
        );

        // 2. LLAMADA A LA IA (VLM)
        // Le pasamos la imagen que vino de Go y nuestro prompt especializado
        StageOneResult aiResult = aiService.callVLM(request.imageUrl(), prompt);

        // 3. FAIL-SAFE: EL VOLANTAZO (Validación de Categoría)
        // Si el Stage 0 (Go) falló, lanzamos excepción para re-rutear el caso
        if (!aiResult.isCategoryValid()) {
            throw new CategoryMismatchException("DERMATOLOGIA", aiResult.confirmedCategory());
        }

        //NO HACE FALTA
        //new DermatologyDetails(aiResult.summary(), aiResult.getBool("itching"), aiResult.getString("lesionColor"),

        // 5. MAPEO ESTRICTO (la IA tiene que devolver exacto o error)
        try {
            // Sacamos el mapa 'details' y lo convertimos al Record de un golpe
            DermatologyDetails details = mapper.convertValue(
                aiResult.specialtyDetails(),
                DermatologyDetails.class
            );

            // 6. INSERT en entity base (SQL)
            // entity.setCleanedMedicalHistory(aiResult.cleanedMedicalHistory());
            // entity.setCleanedPatientInput(aiResult.cleanedPatientInput());
            // 7. INSERT en details JSONB de entity
            entity.getMedicalData().put("specialtyDetails", details); // ¡A LA MOCHILA!

            // Estado actualizado
            entity.setStatus(TriageStatus.ST1_SPECIALIST_ACCEPTED);

        } catch (Exception e) {
            log.error("La IA se inventó los campos: {}", e.getMessage());
            throw new MedicalDataIncompleteException("Contrato de Derma roto por la IA.");
        }
    }

    @Override
    public void conductStage2(TriageCase entity) {
        // TODO: Implementar búsqueda en Vector DB de patologías cutáneas
        // y análisis de riesgo SQL basado en el record 'details'.
    }

    @Override
    public void conductStage3(TriageCase entity) {
        // TODO: LLM Pro genera el diagnóstico sugerido y nivel de urgencia final.
    }
}
