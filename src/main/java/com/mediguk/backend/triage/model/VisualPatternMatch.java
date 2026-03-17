package com.mediguk.backend.triage.model;

/**
 * EVIDENCIA VISUAL/SEMÁNTICA (Vector DB)
 * Basado en la similitud de la imagen y la descripción clínica.
 */
public record VisualPatternMatch(
    String historicalCaseId,     // El ID del caso del pasado
    double similarityScore,      // 0.98 (Casi idéntico)
    String historicalDiagnosis,  // Qué era aquel caso
    String historicalImageUrl,   // Para que el médico pueda comparar las fotos
    boolean isHighRisk           // Si aquel caso fue grave
) {}

// SimilarityMatchPorque son casos reales de tu base de datos. Si el caso está ahí, es porque existió.
// StatisticalRiskPorque es matemática pura. Un modelo de Scikit-learn no "imagina", simplemente dice: "De 10.000 filas, 7.000 son X".

//SIMILARITY MATCH (VISUAL/ SEMANTIC con VectorDB) (Machine Learning) (Searcher) (con datos de bd SQL)

//1. DATA ----> Numeros(VECTORES)
//  - Visual image --> modelo CLIP --> Vectores
//  - Semantic text --> modelo Ada --> Vectores

//2. Vectores ----> Guardar en Vector DB 

//3. Buscar en Vectores DB la CERCANIA MATEMATICA ----> Resultado de mas cercanos

//4. Resultado X de mas cercanos (id) ---> Busqueda SQL en BD normal by id

//5. Create object SimilarityMatch: diagnosticos, URL de foto y score (que tan cercano matematically)
