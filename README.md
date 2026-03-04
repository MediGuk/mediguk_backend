# Mediguk Backend

Service backend de la plataforma Mediguk desarrollado con Spring Boot.

## Entorno

Versiones requeridas:
- Java 21 (verificar con `java -version` y `javac -version`)
- PostgreSQL

## Configuración

1. Clona el repositorio.
2. Crea el archivo de entorno:
   ```bash
   cp .env.example .env
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

*Mas documentos en docs/*