// package com.mediguk.backend.triage.service;

// import org.springframework.stereotype.Service;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// import com.mediguk.backend.triage.entity.TriageCase;
// import com.mediguk.backend.triage.repository.TriageRepository;
// import com.mediguk.backend.triage.model.StageTwoResult; // El Record que envuelve el resultado

// /**
//  * LA FACHADA CIENTÍFICA:
//  * Orquesta la búsqueda en Vector DB y los cálculos estadísticos.
//  */
// @Slf4j
// @Service
// @RequiredArgsConstructor
// public class StageTwoFacade {

//     private final VectorDbService vectorDb;
//     private final StatisticalMotor statistical;
//     private final TriageRepository repository;

//     public StageTwoResult getFullScientificEvidence(TriageCase entity) {
//         log.info("Iniciando recolección de evidencia científica para el caso: {}",
// entity.getId());

//         // 1. Buscamos casos parecidos (Vectores)
//         var matches = vectorDb.findTopMatches(entity.getOptimizedImageUrl());

//         // 2. Calculamos riesgo (Estadística)
//         var risk = statistical.calculateProbability(entity.getMedicalData());

//         log.info("Evidencia recolectada: {} matches, Riesgo: {}%", matches.size(), risk * 100);

//         return new StageTwoResult(matches, risk);
//     }
// }
