package com.mediguk.backend.triage.service;

// ESTA ABAJO EL TODO EL CODIGO ****************************

// *********************TriageCase + SpecializedCase (STAGE 0,1): Descripcion
// ***************************
// ***************************************************************************************************************** */
//    STAGE 0 (Go) para filtro de calidad super rapido:
//          Does:
//              - Audio a texto con Whisper + LLama
//              - Imagen a VLM light para pedir sacar mejor calidad u otro angulo
//              - Contextual Probing de paciente -> Logica de flujo Go para preguntar mas cosas si
// hace falta y mejorar contexto
//              - Procesamiento de imagen -> Compresion a formatos compatibles y no mucho peso
//              - Deteccion de urgenecia -> Logica Go detecta keywords y patrones y manda por
// Webhook a spring boot urgente

//    STAGE 1 (VLM Cloud/Local) para descripcion visual (use Pytorch to train and embedding can
// also). NO photo , then LLM:
//          Receive:
//              - Imagen optimizada
//              - Contexto del paciente: edad, sexo y zona del cuerpo (anatom site)
//              - Relato del paciente
//              - Historial clinico del paciente
//          Return (json):
//              - Descripcion visual
//              - Hallazgos claves: asimetria, colores (regla ABCDE derma)
//              - Categoria probable: DermaCase or RerpiratoryCase
//              - Extraer entidades medicas (todas) del historial clinico (todas) (para tener mas
// limpio y no mucho texto)
//              - Relato estructurado del paciente
//              - Sugerencia diagnostico

// *********************PatternMatch + TriageAnalysisResponse (STAGE 2,3): IA Analysis
// *******************************
// ***************************************************************************************************************** */
//    STAGE 2 (Vector DB) para comparar con tus millon de casos en embedding (patternMatch, RAG,
// data analysis)(CASOS REALES)
//          Receive:
//              - All of stage 1
//          Process:
//              - Embedding Generation: Convierte lo recibido del Stage 1 en un vector (lista de
// numeros)
//              - Vector search: Busca en Vector DB (ChormaDB/Pinecone) los x casos mas parecidos
// (RAG)
//              - Statistical Scoring: Usa scikit-learn para comparar datos tabulares (varon 50
// anos con sangrado X probalbidad de..)
//          Return (json) (PatternMatch []):
//              - SIMILITUD Vector DB: Busca por descripicon iamgen o similitud semantica de la
// descripcion de lo que h docho  y tal y resultado ->                hay 5 casos clincios con
// misma descripcion, diagnostico , imagen visual
//              - PROBABILIDAD Statistical: x personal con este perfil, historial y sintomas -> el
// 70% tiene x enfermedad . A parte alerta de riesgo + descarte
//
//     STAGE 3 (LLM Razonador) (THEORY: MEDICAL KNOWLEDGE + LLM TRAINNIG)
//          Receive:
//              - From Stage 1: all with sugerencia diagnostico included
//              - From Stage 2: all the true
//              - From Historial Clinico: lo limpiado de stage 1
//          Process:
//              - Saca infome UNICAMENTE de evidencias de Stage 2
//              - Prioridad absoluta a Stage 2 pero puedes contrarrestras con VLM para mayor
// accuracy
//              - NO inventar diagnostico que no este en base de datos , si no, va a medico de
// cabecera directo as unknown
//          Return(json)(triageAnalysisResponse):
//              - finalUrgency (0-10)
//              - diagnosisSuggestion (basado en la evidencia eee)
//              - highlitedHistoryPoints (subraaydo de historial clincio utlizoa relacionado en le
// caso)
//              - matchedPatterns (PatternMatch)
//              - correlationNote (todo bien razonado el diagnosis y todo)
//              - recommendation (tratamiento o pautas a seguir)
//              - confidenceLevel (contradice entre stage 1 y stage 2??)
//              - AssignedSpeciality (derma, trauma..) / healthcareLevel (cabecera, enfermero...)
// / unknowFactor (true or false)

//// *********************FinalSanitaryResponse (Datos del caso (0,1) + Datos del analisis (2,3))
// ***************************
// ***************************************************************************************************************** *******/
//      ENVIO FINAL al sanitatio
//              - TriageCase medicalData
//              - TriageAnalysisResponse aiAnalysis
//

// https://www.isic-archive.com/

import com.mediguk.backend.triage.dto.request.TriageRequest;
import com.mediguk.backend.triage.entity.TriageCase;
import com.mediguk.backend.triage.entity.TriageStatus;
import com.mediguk.backend.triage.exception.CategoryMismatchException;
import com.mediguk.backend.triage.exception.UUIDCollisionException;
import com.mediguk.backend.triage.factory.TriageStrategyFactory;
import com.mediguk.backend.triage.repository.TriageRepository;
import com.mediguk.backend.triage.strategy.TriageStrategy;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class TriageService {

  @Autowired private TriageRepository repository;

  @Autowired private TriageStrategyFactory factory;

  @Transactional
  public TriageCase processTriage(TriageRequest request) {
    // 1. OBTENER O CREAR EL CASO
    // 1. Buscamos si el ID ya existe en nuestra DB
    Optional<TriageCase> existingCase = repository.findById(request.id());

    if (existingCase.isPresent()) {
      // ¿Es el mismo paciente reintentando o es un choque de UUIDs?
      if (!existingCase.get().getPatientId().equals(request.patientId())) {
        // ¡EL MILAGRO OCURRIÓ! Dos pacientes distintos con el mismo UUID
        log.error("¡ALERTA ROJA! Choque de UUID detectado: {}", request.id());
        throw new UUIDCollisionException("El ID ya existe para otro paciente. Go debe regenerar.");
      }
      // Si es el mismo paciente, seguimos adelante (es un reintento normal)
    }

    // 2. Si no existe, creamos el nuevo caso
    TriageCase entity =
        existingCase.orElseGet(
            () ->
                new TriageCase(
                    request.id(), request.patientId(), request.rawInput(), TriageStatus.CREATED));

    // 2. RELLENO ADMINISTRATIVO
    if (request.imageUrl() != null) {
      entity.setOptimizedImageUrl(request.imageUrl());
    }
    // entity.setAudioUrl(request.audioUrl());
    // Ya no es "Eyes on", ahora seguimos tu Enum
    // entity.setStatus(TriageStatus.ST1_GEN_CONFIRMED);

    // 3. SELECCIÓN DE ESTRATEGIA INICIAL
    String categoryToUse = request.suggestedCategory();

    try {
      executeStage1WithStrategy(entity, request, categoryToUse); // FACTORY AND ALL!!!!!
    } catch (CategoryMismatchException e) {
      // --- EL VOLANTAZO DE DIGNIDAD ---
      log.warn(
          "Go falló. La IA dice que no es {}, es {}. Reintentando...",
          e.getOldCategory(),
          e.getNewCategory());

      // Opcional: podrías guardar el estado de REJECTED aquí antes de flush
      entity.setStatus(TriageStatus.REJECTED_BY_SPECIALIST);
      repository.saveAndFlush(entity); // <--- AQUÍ: Guardas el rastro del fallo antes de reintentar

      // Reintento con el nuevo médico
      executeStage1WithStrategy(entity, request, e.getNewCategory());
    }

    // 4. GUARDADO FINAL DEL STAGE 1 (Sello de éxito)
    entity.setStatus(TriageStatus.ST1_SPECIALIST_ACCEPTED);
    // RETURN para la demo
    return repository.saveAndFlush(entity); // Aseguras que el Stage 1 se cierra y se escribe ya.

    // FACADE se utilzias para hacer como metodos helper para el service y asi no tenemos 2000
    // lineas de codigo
    // StageTwoResult evidence = stageTwoFacade.getFullScientificEvidence(entity);
    // executeStage3Verdict(entity, evidence);
  }

  private void executeStage1WithStrategy(
      TriageCase entity, TriageRequest request, String category) {
    entity.setCategory(category);
    TriageStrategy strategy = factory.getStrategy(category);

    // El especialista lanza su prompt, llama al VLM y llena la mochila JSONB
    strategy.conductStage1(entity, request);
  }
}
