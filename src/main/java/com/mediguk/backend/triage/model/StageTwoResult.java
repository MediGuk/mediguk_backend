package com.mediguk.backend.triage.model;

import java.util.List;

public record StageTwoResult(
    //1. Evidencia Visual/Semántica (RAG - Vector DB)
    // Qué es: "He buscado en la base de datos y estos 5 casos tienen una imagen o una descripción de síntomas casi igual".
    // TOP 5 Casos reales (Lo que "Vemos" en el pasado)
    List<VisualPatternMatch> visualSimilitudes, 
    
    // 2. Evidencia Estadística (Scikit-Learn / Tabular)
    // Qué es: "Según el perfil (Hombre, 50 años, fumador), el 70% de los casos históricos con este síntoma terminaron en X".
    // TOP Probabilidades (Lo que los "Números" dicen)
    List<StatisticalPattern> statisticalProbabilities,
    
    // Un flag de alerta si algo huele muy mal en los datos
    boolean highRiskPatternDetected
) {}
