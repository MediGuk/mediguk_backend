# Mediguk Backend

Service backend de la plataforma Mediguk desarrollado con Spring Boot.

## Entorno

Versiones requeridas:
- Java 21 (verificar con `java -version` y `javac -version`)
- SpringBoot 4.0.3
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

---

## Documentación Técnica

* **[Flujos de Negocio](./docs/diagrams/):** Diagramas de secuencia (Mermaid) con la lógica de negocio.
* **[Modelo de Datos](./docs/database/):** Diagrama Entidad-Relación (ERD).
* **[Architecture Decision Record](./docs/adr/):** Explicacion y aprobacion de decisiones tecnicas.
* **API Reference (Swagger):** Documentación interactiva de endpoints disponible en `/swagger-ui.html` (generada automáticamente con SpringDoc). Added annotations to know what does endpoints and errors. 

> **Tip:** Si usas VS Code, instala la extensión "Markdown Preview Mermaid Support" para previsualizar los diagramas directamente.