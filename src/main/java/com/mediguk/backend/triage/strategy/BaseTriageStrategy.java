package com.mediguk.backend.triage.strategy;

public abstract class BaseTriageStrategy implements TriageStrategy {
    
    // Aquí puedes meter métodos que TODOS usen, 
    // como formatear el prompt o validar que la imagen existe.
    
    protected void logStageStep(String caseId, String step) {
        System.out.println("Caso " + caseId + ": Ejecutando " + step);
    }
}
