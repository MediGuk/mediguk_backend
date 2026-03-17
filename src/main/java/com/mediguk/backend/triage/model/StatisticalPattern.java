package com.mediguk.backend.triage.model;

import java.util.List;

/**
 * TENDENCIA ESTADÍSTICA (Lo que los números dicen de este perfil)
 */
public record StatisticalPattern(
    String suspectedDiagnosis,   // "Melanoma", "Dermatitis"
    double probabilityScore,     // 0.85
    String humanReadableNote,    // "El 85% de varones >50 con este síntoma..."
    boolean isHighRisk,
    List<String> riskFactors     // ["Edad", "Localización"]
) {}

// SimilarityMatchPorque son casos reales de tu base de datos. Si el caso está ahí, es porque existió.
// StatisticalRiskPorque es matemática pura. Un modelo de Scikit-learn no "imagina", simplemente dice: "De 10.000 filas, 7.000 son X".

//STATISTICAL RISK (SQL normal y variables) (Data analysis) (busca patrones)

//1. TriageCase ----> Formula Matematica de probabilidad (Scikit-learn/Regresion) (entrenada con BD sql)

//2. Resultado de probabilidades de diagnosticos 

//3. Create object StatisticalRisk: diagnosiis, probalbily (%), riskFactors (parametros pa probabilidad), isHighrisk?


// En model/StatisticalRisk.java
// public record StatisticalRisk(
//     String suspectedDiagnosis, // "Carcinoma"
//     double probabilityScore,   // 0.80
//     List<String> drivingFactors, // ["Edad avanzada", "Sangrado activo"]
//     boolean isHighRisk         // true (porque 80% es una barbaridad)
// ) {}
