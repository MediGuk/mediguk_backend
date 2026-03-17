# 🥊 Herencia + Factory vs. Strategy + Factory (Composition)

## 📋 Resumen del Combate Arquitectónico

En el desarrollo de software médico, la rigidez es el enemigo. A continuación, desglosamos por qué hemos pivotado de una estructura hereditaria a una basada en composición con **JSONB** y **Strategy Pattern**.

---

### 🏛️ 1. El Enfoque Antiguo: Herencia Rígida
*Estructura: `CommonTriageStrategy` -> `DermatologyStrategy`*

* **El Problema:** Obliga a todas las especialidades a ser "hijas" de un mismo padre. Si el padre cambia, todos los hijos pueden romperse.
* **La Limitación:** Si Dermatología necesita 20 campos y Respiratorio solo 5, la base de datos se llena de columnas vacías (`NULL`) o necesitas tablas separadas con `JOINs` pesados.
* **El Riesgo:** Requiere `Casting` manuales en el código (ej. `(DermaCase) entity`), lo que provoca errores en tiempo de ejecución si la categoría no coincide.

---

### 🚀 2. El Enfoque Mediguk: Strategy + Mochila JSONB
*Estructura: `TriageStrategy (Interface)` + `medical_data (JSONB)`*

#### A. Flexibilidad que Salva Vidas 🛡️
La medicina evoluciona. Si mañana aparece un nuevo biomarcador para el cáncer de piel, tu **Strategy** simplemente lo añade a su **Record** y lo mete en la mochila JSONB. 
* **Sin paradas de sistema.**
* **Sin migraciones de tablas SQL.**
* **Sin alterar el corazón del sistema.**

#### B. Complejidad en el Código, Simplicidad en la Data 🧠
La base de datos se mantiene limpia y rápida (SQL puro para IDs y estados). La "magia" ocurre en Java:
* **Tipado Fuerte:** Usamos `Records` de Java para que cada especialidad valide su información. Aunque se guarde como JSON, en Java es un objeto médico real e indestructible.
* **Dignidad Lógica:** Cada etapa (Stage 1, 2 y 3) sella su propio "sobre" dentro de la mochila, creando un historial clínico inmutable y fácil de auditar.

#### C. Estándar Internacional (FHIR) 🌍
Estamos alineados con los estándares de élite como **HL7 FHIR**. Los mejores hospitales del mundo usan estructuras base con extensiones flexibles. Mediguk no es un "postureo" técnico; es arquitectura resiliente de grado médico.

---

### ⚖️ Veredicto Final

| Característica | Herencia Clásica | Strategy + Composition (Mediguk) |
| :--- | :--- | :--- |
| **Mantenimiento** | Pesado y arriesgado | Ágil (Añadir/Quitar sin miedo) |
| **Base de Datos** | Tablas rígidas o mil JOINs | Una sola mochila JSONB rápida |
| **Seguridad** | Fallos por Casting | Validada por Records e IA |
| **Evolución** | Requiere rediseño | Orgánica y constante |

---

> **"No estamos ahorrando trabajo, estamos construyendo un búnker. La medicina no cabe en una cuadrícula fija; nuestra arquitectura asegura que ningún matiz se pierda."** 🚀🔥🏁