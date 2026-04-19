package com.mediguk.backend.triage.controller;

import com.mediguk.backend.triage.dto.request.TriageRequest;
import com.mediguk.backend.triage.dto.response.DemoStageOneResponse;
import com.mediguk.backend.triage.entity.TriageCase;
import com.mediguk.backend.triage.service.TriageService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/triage")
@RequiredArgsConstructor
public class TriageController {

  private final TriageService triageService;

  @GetMapping("/history")
  public ResponseEntity<List<DemoStageOneResponse>> getTriageHistory(
      @AuthenticationPrincipal String patientId) {
    log.info("📡 [JAVA GATEWAY] Consultando historial para el paciente autenticado: {}", patientId);

    UUID patientUuid = UUID.fromString(patientId);
    List<TriageCase> history = triageService.getPatientHistory(patientUuid);
    List<DemoStageOneResponse> response =
        history.stream()
            .map(
                entity ->
                    new DemoStageOneResponse(
                        entity.getId(),
                        entity.getCategory(),
                        entity.getStatus(),
                        entity.getFullTranscript(),
                        entity.getMedicalData()))
            .collect(Collectors.toList());

    return ResponseEntity.ok(response);
  }

  /**
   * EL PUNTO DE ENTRADA (STAGE 1): Aquí es donde Go nos manda el ID, el PatientId y el RawInput.
   */
  @PostMapping("/process")
  public ResponseEntity<DemoStageOneResponse> processTriage(
      @Valid @RequestBody TriageRequest request) {
    log.info("--- NUEVO CASO RECIBIDO DESDE GO ---");
    log.info(
        "ID Caso: {} | Paciente: {} | Categoría Sugerida: {}",
        request.id(),
        request.suggestedCategory());

    // Disparamos el motor de Triage (Stage 1: IA Especialista)
    TriageCase entityProcesada = triageService.processTriage(request);

    // Creamos la respuesta para la Demo
    DemoStageOneResponse response =
        new DemoStageOneResponse(
            entityProcesada.getId(),
            entityProcesada.getCategory(),
            entityProcesada.getStatus(),
            entityProcesada.getFullTranscript(),
            entityProcesada.getMedicalData() // Aquí va tu DermatologyDetails dentro del Map
            );

    log.info("--- STAGE 1 COMPLETADO CON ÉXITO PARA EL CASO: {} ---", request.id(), response);

    return ResponseEntity.ok(response);
  }
}
