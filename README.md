# Mediguk Backend

Service backend de la plataforma Mediguk desarrollado con Spring Boot.

## Entorno

Versiones requeridas:
- JDK Java 21 (verificar con `java -version` y `javac -version`)
- SpringBoot 4.0.3
   migration help: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide#starters
- PostgreSQL

## Configuración

1. Clona el repositorio.
2. Crea el archivo de entorno:
   ```bash
   cp .env.dev .env
   OR
   cp .env.prod .env
   ```
3. Ejecuta el script de configuración del proyecto:
   ```bash
   ./scripts/setup.sh
   ```
4. Inicia el servidor:
   ```bash
   ./scripts/start.sh
   ```
   El servidor se ejecutará en: http://localhost:8080

5. Ejecuta las pruebas:
   ```bash
   ./scripts/test.sh
   ```

## Problemas
 1. Dependencias y cache: CTRL + shift + P -> Java: Clean Java Language Server Workspace
---

## Documentación Técnica

* **[Flujos de Negocio](./docs/diagrams/):** Diagramas de secuencia (Mermaid) con la lógica de negocio.
* **[Modelo de Datos](./docs/database/):** Diagrama Entidad-Relación (ERD).
* **[Architecture Decision Record](./docs/adr/):** Explicacion y aprobacion de decisiones tecnicas.
* **API Reference (Swagger):** Documentación interactiva de endpoints disponible en `http://localhost:8080/swagger-ui.html` (generada automáticamente con SpringDoc OpenApi). Con anotaciones para saber que hace el endpoint.

> **Tip:** Si usas VS Code, instala la extensión "Markdown Preview Mermaid Support" para previsualizar los diagramas directamente.

## 🧪 Testing y Calidad

Stack de validación para asegurar un código robusto y mantenible:

* **JUnit 5 & Mockito:** Framework base para pruebas unitarias y simulación de dependencias.
* **H2 Database:** Base de datos en memoria para tests de integración rápidos sin depender de PostgreSQL.
* **JaCoCo:** Agente que mide el **Coverage** (qué porcentaje de líneas de código "pisan" los tests).
* **SonarQube & SonarLint:** Análisis estático de bugs, vulnerabilidades y cumplimiento del **Quality Gate (mínimo 50% coverage)**.

### Ejecutar análisis completo
Para correr los tests y enviar el reporte de cobertura al panel de Sonar:
```bash
./mvnw clean verify sonar:sonar -Dsonar.token=TU_TOKEN_AQUÍ
