package com.mediguk.backend.triage.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediguk.backend.triage.model.StageOneResult;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/** EL MOTOR DE LA IA Este servicio se encarga de la comunicación bruta con el VLM (Gemini/GPT). */
@Slf4j
@Service
public class AIService {

  @Value("${app.ai.gemini.api-key}")
  private String apiKey;

  private ObjectMapper mapper; // El notario que parsea el JSON
  private final RestClient restClient = RestClient.builder().build(); // Para llamada de gemini api

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
      log.info("--- MODO MULTIMODAL ACTIVO  ---");
      // Aquí la lógica de Gemini para adjuntar la URL
    } else {
      log.info("--- MODO TEXTO PURO  ---");
      // Aquí la lógica para enviar solo el prompt
    }

    // 1. EL PAYLOAD: Replicamos el objeto de NestJS (contents -> parts -> text)
    // Usamos Map.of para que sea corto y sin chapa
    var payload =
        Map.of(
            "contents",
            List.of(
                Map.of(
                    "parts",
                    List.of(
                        Map.of(
                            "text",
                            expertPrompt + (imageUrl != null ? "\nImagen: " + imageUrl : ""))))));

    try {
      // 2. Llamamos a la api con restClinent tipo fetch()
      String rawResponse =
          restClient
              .post()
              .uri(
                  "https://generativelanguage.googleapis.com/v1beta/models/"
                      + "gemini-flash-latest"
                      + ":generateContent?key="
                      + apiKey)
              .contentType(MediaType.APPLICATION_JSON)
              .body(payload)
              .retrieve()
              .body(String.class);

      // 3. EL PARSEO: Entramos en el árbol de Google (candidates[0].content.parts[0].text)
      JsonNode root = mapper.readTree(rawResponse);
      String aiText =
          root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

      // 5. LA LIMPIEZA: Lo que hacías en NestJS (fuera ```json y fuera ```)
      String cleanJson =
          aiText
              .replaceAll("(?s)```json\\s*(.*?)\\s*```", "$1") // Quita el bloque completo
              .replaceAll("```", "") // Por si acaso quedan sueltos
              .trim();

      log.info("JSON limpio recibido de la IA");

      // 2. PARSEO AL CONTENEDOR (StageOneAIResult)
      // Gracias al @JsonProperty que pusimos, Jackson lo mapea solo.
      StageOneResult result = mapper.readValue(cleanJson, StageOneResult.class);

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
