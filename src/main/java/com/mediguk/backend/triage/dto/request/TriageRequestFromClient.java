package com.mediguk.backend.triage.dto.request;
// Del cliente pasa por Go para limpiar y llegar bien
import java.util.Map;
import java.util.UUID;

public record TriageRequestFromClient(
    UUID id,
    String patientId,
    String suggestedCategory, // "DERMATOLOGIA", "RESPIRATORIO", etc. // Lo pone el cliente 
    String rawInput, // Lo que salió de Whisper + Llama
    String imageUrl, // La URL de S3 que mandó Go // optional
    Map<String, Object> extraData // <-- ¡LA CLAVE! Aquí viene el "anatomSite", "fever", etc.
    ) {
        // TIP: Puedes añadir un método de conveniencia dentro del Record
        public boolean hasImage() {
            return imageUrl != null && !imageUrl.isBlank();
        }
    }

//******************************************************** */

// Llega el Request.

// Go recoge la voz y lo convierte en texto ordenado y partes importantes con ultra rapida con Whisper + LLama.

// Go envia al paciente esa transcripcionde manera ordenada y entendible y el user da ok si esta bien.

// Go analiza lo enviaod el user junto a la imagen si hay y le da sugerencias : mas preguntas dependiendo l oqeu tienie , confiramr category , sugerir saar mejro foto si ha ce falta ........

