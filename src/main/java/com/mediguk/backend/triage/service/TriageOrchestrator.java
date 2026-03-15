package com.mediguk.backend.triage.service;

public class TriageOrchestrator {

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

}

// https://www.isic-archive.com/

// @Service
// public class TriageOrchestrator {

//     public TriageAnalysisResponse conductFullAnalysis(TriageRequest request) {

//         // STAGE 1: Análisis Visual
//         VlmResult vlm = vlmService.analyzeImage(request.image());

//         // STAGE 2: Búsqueda de patrones (Vector DB) RAG
//         List<PatternMatch> matches = vectorDbService.findSimilarPatterns(vlm.description());
//         Data analysis

//         // STAGE 3: Correlación e Historial (El LLM "une" los puntos)
//         PatientHistory history = historyService.getHistory(request.patientId());

//         // El LLM recibe TODO y te devuelve el Record que diseñamos
//         TriageAnalysisResponse finalAnalysis = llmService.correlateAll(vlm, matches, history);

//         // GUARDAR EN DB: Aquí mapeas el Record a tu Entity TriageCase
//         triageRepository.save(new DermatologyCase(finalAnalysis, request.patientId()));

//         return finalAnalysis;
//     }
// }
