package com.mediguk.backend.triage.entity;

/**
 * EL MAESTRO DE CEREMONIAS
 * Este Enum controla que ningún Stage se salte la cola.
 */

/**
 * EL CICLO DE VIDA DEL CASO (La Historia Clínica en Mediguk)
 */
public enum TriageStatus {
    // --- STAGE 0 (La Recepción de Go) ---
    CREATED,                // Recién llegado de Go (Sin procesar)

    // --- STAGE 1 (El Analista: Triple Check) ---
    ST1_GEN_CONFIRMED,      // VLM General (Stage 0.5) confirmó la categoría
    ST1_SPECIALIST_ACCEPTED,// La Strategy validó, sacó los Details y llenó la mochila
    
    // --- STAGE 2 (El Científico: Evidencia) ---
    ST2_FACTS_COLLECTED,    // Vector DB y patrones SQL están ya en la mochila

    // --- STAGE 3 (El Juez: Sentencia) ---
    ST3_JUDGED,             // El LLM Juez dictó sentencia y urgencia final
    
    // --- FINALES ---
    COMPLETED,              // Caso cerrado y enviado de vuelta al paciente
    
    // --- EXCEPCIONES (Los Cortafuegos) ---
    REJECTED_BY_SPECIALIST, // El "Volantazo": la IA dijo "Dermatología no es esto"
    FAILED                  // Error técnico (API caída, timeout, etc.)
}
