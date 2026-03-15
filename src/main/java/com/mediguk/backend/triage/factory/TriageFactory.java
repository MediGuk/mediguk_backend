// package com.mediguk.backend.triage.factory;

// import com.mediguk.backend.triage.entity.TriageCase;
// import com.mediguk.backend.triage.entity.cases.DermatologyCase;
// import com.mediguk.backend.triage.entity.cases.RespiratoryCase;
// import com.mediguk.backend.triage.strategy.TriageStrategy; // Asumiendo que esta es la ruta
// import java.util.List;
// import java.util.Map;
// import java.util.stream.Collectors;
// import org.springframework.stereotype.Component;

// @Component
// public class TriageFactory {

//   // Registramos las estrategias de procesamiento
//   private final Map<String, TriageStrategy> strategies;

//   /**
//    * Spring inyecta automáticamente todas las clases que implementen TriageStrategy. Es la magia
// de
//    * la inversión de control (IoC).
//    */
//   public TriageFactory(List<TriageStrategy> strategyList) {
//     this.strategies =
//         strategyList.stream()
//             .collect(
//                 Collectors.toMap(
//                     strategy -> strategy.getCategory().toUpperCase(), strategy -> strategy));
//   }

//   /** Este método crea la INSTANCIA de la Entity (la tabla de la DB). */
//   public TriageCase createCase(String category) {
//     if (category == null) {
//       throw new IllegalArgumentException("La categoría no puede ser nula");
//     }

//     return switch (category.toUpperCase()) {
//       case "DERMATOLOGIA" -> new DermatologyCase();
//       case "RESPIRATORIO" -> new RespiratoryCase();
//         // Aquí irás añadiendo los nuevos: MUSCULOESQUELETICO, INFECCION, etc.
//       default -> throw new IllegalArgumentException("Categoría desconocida: " + category);
//     };
//   }

//   /** Este método te da el SERVICE (la Strategy) que sabe cómo llamar a la IA. */
//   public TriageStrategy getStrategy(String category) {
//     if (category == null) return null;
//     return strategies.get(category.toUpperCase());
//   }
// }
