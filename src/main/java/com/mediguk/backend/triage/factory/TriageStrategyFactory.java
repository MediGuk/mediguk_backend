package com.mediguk.backend.triage.factory;

import com.mediguk.backend.triage.strategy.TriageStrategy;
// Usando el Enum que dijimos
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TriageStrategyFactory {

  private final Map<String, TriageStrategy> strategies;

  /**
   * El constructor hace todo el trabajo sucio una sola vez al arrancar. Registra todos los
   * "médicos" que encuentre en el proyecto.
   */
  public TriageStrategyFactory(List<TriageStrategy> strategyList) {
    this.strategies =
        strategyList.stream()
            .collect(
                Collectors.toMap(
                    strategy -> strategy.getCategory().toUpperCase(), strategy -> strategy));
    log.info("Búnker cargado: {} estrategias médicas registradas.", strategies.size());
  }

  /** El Service pide un experto y nosotros se lo damos. */
  public TriageStrategy getStrategy(String categoryName) {
    // 1. Normalizamos la entrada
    String key = (categoryName == null) ? "GENERAL" : categoryName.toUpperCase();

    // 2. Buscamos en el mapa
    TriageStrategy strategy = strategies.get(key);

    // 3. Fallback: Si Go manda una categoría que no hemos programado aún
    if (strategy == null) {
      log.warn("Categoría '{}' no reconocida. Derivando a medicina GENERAL.", key);
      return strategies.get("GENERAL");
    }

    return strategy;
  }
}
