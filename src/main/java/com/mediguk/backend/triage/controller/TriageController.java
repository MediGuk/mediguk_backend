package com.mediguk.backend.triage.controller;

import com.mediguk.backend.triage.dto.request.TriageRequestFromClient;
import com.mediguk.backend.triage.dto.response.DemoStageOneResponse;
import com.mediguk.backend.triage.entity.TriageCase;
import com.mediguk.backend.triage.service.TriageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/triage")
@RequiredArgsConstructor
public class TriageController {

  private final TriageService triageService;

  /**
   * EL PUNTO DE ENTRADA (STAGE 1): Aquí es donde Go nos manda el ID, el PatientId y el RawInput.
   */
  @PostMapping("/process")
  public ResponseEntity<DemoStageOneResponse> processTriage(
      @Valid @RequestBody TriageRequestFromClient request) {
    log.info("--- NUEVO CASO RECIBIDO DESDE GO ---");
    log.info(
        "ID Caso: {} | Paciente: {} | Categoría Sugerida: {}",
        request.id(),
        request.patientId(),
        request.suggestedCategory());

    // Disparamos el motor de Triage (Stage 1: IA Especialista)
    TriageCase entityProcesada = triageService.processTriage(request);

    // Creamos la respuesta para la Demo
    DemoStageOneResponse response =
        new DemoStageOneResponse(
            entityProcesada.getId(),
            entityProcesada.getPatientId(),
            entityProcesada.getCategory(),
            entityProcesada.getStatus(),
            entityProcesada.getMedicalData() // Aquí va tu DermatologyDetails dentro del Map
            );

    log.info("--- STAGE 1 COMPLETADO CON ÉXITO PARA EL CASO: {} ---", request.id(), response);

    return ResponseEntity.ok(response);
  }
}
