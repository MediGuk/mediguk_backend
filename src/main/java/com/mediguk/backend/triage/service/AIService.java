package com.mediguk.backend.triage.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediguk.backend.triage.model.StageOneResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** EL MOTOR DE LA IA Este servicio se encarga de la comunicación bruta con el VLM (Gemini/GPT). */
@Slf4j
@Service
public class AIService {

  private ObjectMapper mapper; // El notario que parsea el JSON

  // Al tener un solo constructor, Spring inyecta el ObjectMapper automáticamente
  public AIService(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  // FUTURE:Un mismo ia interfaz pero hacer files distintos para cada modelo distinto
  /** Llama al VLM con una imagen y un prompt experto. */
  public StageOneResult callVLM(String imageUrl, String expertPrompt) {
    log.info("Invocando a la IA con prompt experto...");

    // Verificamos si tenemos "ojos" (imagen) con texto o solo "oído" (texto)
    if (imageUrl != null && !imageUrl.isBlank()) {
      log.info("--- MODO MULTIMODAL ACTIVO (Gemini) ---");
      // Aquí la lógica de Gemini para adjuntar la URL
    } else {
      log.info("--- MODO TEXTO PURO (Llama/Grok) ---");
      // Aquí la lógica para enviar solo el prompt
    }

    try {
      // 1. SIMULACIÓN DE LLAMADA API (Aquí iría tu FeignClient o WebClient a Gemini/GPT)
      // Imagina que la IA nos devuelve el JSON que pactamos:
      String rawJsonResponse = simulateAiResponse();

      // 2. PARSEO AL CONTENEDOR (StageOneAIResult)
      // Gracias al @JsonProperty que pusimos, Jackson lo mapea solo.
      StageOneResult result = mapper.readValue(rawJsonResponse, StageOneResult.class);

      log.info("IA ha respondido. Categoría confirmada: {}", result.confirmedCategory());
      return result;

    } catch (Exception e) {
      log.error("¡ERROR EN EL MOTOR DE IA! El JSON de la IA no es digno.", e);
      throw new RuntimeException("La IA ha fallado o ha devuelto basura.");
    }
  }

  /** ESTO ES LO QUE DEVOLVERÍA LA IA (Simulado para que lo veas) */
  private String simulateAiResponse() {
    return """
            {
              "confirmedCategory": "DERMATOLOGIA",
              "isCategoryValid": true,
              "summary": "Lesión eritematosa compatible con dermatitis.",
              "mainSymptom": "Picor",
              "details": {
                "itching": true,
                "lesionColor": "Rojizo",
                "texture": "Rugosa",
                "evolution": "3 días"
              }
            }
            """;
  }
}
