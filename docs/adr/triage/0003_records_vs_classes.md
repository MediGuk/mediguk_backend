# Uso de Records sobre Clases para el Despiece Médico (Stage 1)

**Estatus:** ✅ Aceptado  
**Fecha:** 2026-03-16  
**Autor:** mendibot

---

## 1. Contexto
En el **Stage 1**, el sistema debe extraer y almacenar datos específicos de cada especialidad (ej. color de lesión en Derma, saturación de O2 en Respi). Estos datos se almacenan en una mochila **JSONB** dentro de la entidad única `TriageCase`. Necesitamos decidir si estos "despieces" médicos deben ser Clases con herencia o Records con interfaz.

## 2. Decisión
Se decide utilizar **Java Records** que implementan una **Marker Interface** (`SpecialtyDetails`). Se prohíbe el uso de clases mutables y herencia clásica para el transporte de evidencias médicas en el Stage 1.

## 3. Justificación (Dignidad Lógica)

### A. Inmutabilidad (Sello de Evidencia) 🛡️
La medicina se basa en la evidencia capturada en un momento preciso. Los **Records** son inmutables por definición. Esto garantiza que una vez que el Stage 1 "sella" los síntomas, ningún proceso posterior (Stage 2 o 3) pueda modificarlos por error mediante un `setter`.

### B. Serialización JSONB Limpia 🪄
Al usar Records, Jackson (nuestro mapper) no necesita lógica de herencia compleja ni anotaciones pesadas como `@JsonTypeInfo`. El resultado es un JSONB en la base de datos limpio, directo y fácil de leer por cualquier servicio externo.

### C. Reducción de Ruido (Boilerplate) 🧠
El despiece médico es **Data**, no **Lógica**. Los Records eliminan la necesidad de constructores, `equals`, `hashCode` y `toString`, dejando un código fuente que solo muestra lo que importa: los parámetros médicos.

## 4. Estructura de Referencia

| Pieza | Implementación |
| :--- | :--- |
| **Contrato** | `public interface SpecialtyDetails {}` |
| **Especialista** | `public record DermatologyDetails(...) implements SpecialtyDetails {}` |

## 5. Consecuencias
* **Positivo:** Garantía de integridad de datos entre Stages.
* **Positivo:** Facilidad para añadir nuevas especialidades (extensibilidad horizontal).
* **Negativo:** Si se requiere lógica interna muy compleja dentro del despiece (que no debería ocurrir), el Record podría quedarse corto, obligando a mover esa lógica a la `Strategy`.

---
**"En Mediguk, la evidencia es sagrada. Los Records aseguran que lo que el paciente dice, el sistema no lo altere."** 🚀🔥🏁