package com.mediguk.backend.triage.dto.request;
import java.util.List;  // <--- ESTE ES OBLIGATORIO
import java.util.UUID;
// Del cliente pasa por Go para limpiar y llegar bien
import java.util.Map;
import java.util.UUID;

//public record TriageRequest(
//    UUID id, //case id join with patientId
//    UUID patientId, //patient id (no mandar por redis DNI)
//    String suggestedCategory, // "DERMATOLOGIA", "RESPIRATORIO", etc. // Lo pone el cliente
//    String resumeClinic, // Lo que salió de Whisper + Llama
//    String imageUrl, // La URL de S3 que mandó Go // optional
//    Map<String, Object> extraData // <-- ¡LA CLAVE! Aquí viene el "anatomSite", "fever", etc.
//    ) {
//        // TIP: Puedes añadir un método de conveniencia dentro del Record
//        public boolean hasImage() {
//            return imageUrl != null && !imageUrl.isBlank();
//        }
//    }
public record TriageRequest(
    UUID id, //case id join with patientId
    UUID patientId, //patient id (no mandar por redis DNI)
    String suggestedCategory,  // "DERMATOLOGIA", "RESPIRATORIO", etc. // Lo pone el cliente
    String resumeClinic,    // Lo que salió de Whisper + Llama
    String caseTitle,      // <--- ¡Ahora Java sabe el título!
    boolean isEmergency,   // <--- ¡Java puede lanzar alertas reales!
    String imageUrl,        // La URL de S3 que mandó Go // optional
    List<SymptomDTO> symptoms,          // Sintomas con sus atributos
    List<TranscriptEntryDTO> fullTranscript // Preguntas y respuestas
) {// TIP: Puedes añadir un método de conveniencia dentro del Record
    public boolean hasImage()  {
        return imageUrl != null && !imageUrl.isBlank();
    }
}

//// 1. La pieza fundamental del triaje
//public record SymptomDTO(
//    String key,             // ej: "fiebre"
//    String rawEvidence,     // el fragmento literal
//    Map<String, String> attributes // los metadatos (onset, severity, etc.)
//) {}
// TranscriptEntryDTO.java
//public record TranscriptEntryDTO(
//    String question, // La pregunta de la IA (o "INITIAL" en el primer turno)
//    String answer    // La respuesta literal del paciente
//) {}
//******************************************************** */
// @Data
// public class TriageResult {
//     private String userId;
//     private Map<String, String> entidadesDetectadas;
//     private String sistemaAfectado;
//     private List<String> datosFaltantes;
//     private String resumeClinic;
//     private String timestamp;
// }
// {
//   "id": "a1b2c3d4-e5f6-7777-8888-9999aabbccdd",
//   "patientId": "PAC-001",
//   "suggestedCategory": "DERMATOLOGIA",
//   "rawInput": "Tengo una mancha roja en el antebrazo desde hace tres días. Pica mucho.",
//   "imageUrl": null
// }

//******************************************************** */

// Llega el Request.

// Go recoge la voz y lo convierte en texto ordenado y partes importantes con ultra rapida con Whisper + LLama.

// Go envia al paciente esa transcripcionde manera ordenada y entendible y el user da ok si esta bien.

// Go analiza lo enviaod el user junto a la imagen si hay y le da sugerencias : mas preguntas dependiendo l oqeu tienie , confiramr category , sugerir saar mejro foto si ha ce falta ........

