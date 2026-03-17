package com.mediguk.backend.triage.controller;

import com.mediguk.backend.triage.dto.request.TriageRequestFromClient;
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
  public ResponseEntity<String> processTriage(@Valid @RequestBody TriageRequestFromClient request) {
    log.info("--- NUEVO CASO RECIBIDO DESDE GO ---");
    log.info(
        "ID Caso: {} | Paciente: {} | Categoría Sugerida: {}",
        request.id(),
        request.patientId(),
        request.suggestedCategory());

    // Disparamos el motor de Triage (Stage 1: IA Especialista)
    triageService.processTriage(request);

    log.info("--- STAGE 1 COMPLETADO CON ÉXITO PARA EL CASO: {} ---", request.id());

    return ResponseEntity.ok("Stage 1 procesado y guardado en la mochila JSONB.");
  }
}
