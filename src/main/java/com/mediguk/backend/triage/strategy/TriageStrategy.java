package com.mediguk.backend.triage.strategy;

import com.mediguk.backend.triage.entity.TriageCase;
import com.mediguk.backend.triage.dto.request.TriageRequestFromClient;

public interface TriageStrategy {

    String getCategory();

    /**
     * STAGE 1: El Especialista manda.
     * La Strategy llama a la IA con su PROMPT PROPIO,
     * valida el mismatch y rellena la MOCHILA JSONB.
     */
    void conductStage1(TriageCase entity, TriageRequestFromClient request);

    /**
     * STAGE 2: El Científico busca.
     * Acceso a Vector DB / SQL de patologías.
     */
    void conductStage2(TriageCase entity);

    /**
     * STAGE 3: El Juez sentencia.
     * LLM final con toda la info de la mochila.
     */
    void conductStage3(TriageCase entity);
}