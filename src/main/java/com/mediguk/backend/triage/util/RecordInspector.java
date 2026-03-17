package com.mediguk.backend.triage.util;

import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/** EL INSPECTOR DEL BÚNKER: Herramienta estática para no ensuciar los servicios. */
public final class RecordInspector {

  // Constructor privado para que nadie intente hacer "new" de una Utility
  private RecordInspector() {}

  /** Saca los campos de un Record para el prompt. */
  public static List<String> getFields(Class<?> recordClass) {
    if (!recordClass.isRecord()) {
      throw new IllegalArgumentException(
          "¡Error! " + recordClass.getSimpleName() + " no es un Record médico.");
    }

    return Arrays.stream(recordClass.getRecordComponents())
        .map(RecordComponent::getName)
        .collect(Collectors.toList());
  }
}
