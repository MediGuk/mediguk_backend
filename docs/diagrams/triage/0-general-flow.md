```mermaid
graph TD
    A[Paciente / Cliente] -->|1. Foto + Texto| B(Go Backend - Stage 0)
    B -->|2. Crea UUID + Sube Foto| C{Java Backend - Triage}
    
    subgraph "Búnker Java (Triage Logic)"
    C -->|Stage 1| D[IA Especialista: Ojos y Cerebro]
    D -->|Mochila Inicial| E[Stage 2: Evidencia Científica]
    E -->|Vectores + Mates| F[Stage 3: El Juez]
    F -->|Veredicto Final| G[(Postgres JSONB)]
    end
    
    G -->|4. Alerta / Informe| H[Médico Especialista]
    H -->|5. Validación Humana| I[Paciente: Resultado Final]