```mermaid
flowchart LR
    subgraph "TriageService (Java)"
    direction TB
    R[Request de Go] --> ID{¿UUID Existe?}
    ID -- Sí --> E[Escudo: Validar PatientId]
    ID -- No --> C[Constructor 4 Jinetes: Crear Entity]
    
    C --> F[TriageFactory: Dame Strategy]
    F -->|Categoría: DERMA| S[DermatologyStrategy]
    
    S --> V[VLM / LLM: Analizar Imagen + Texto]
    V -->|Error de Categoría| D[Volantazo de Dignidad: Reintentar con otra Cat]
    V -->|Éxito| M[Llenar Mochila JSONB: DermatologyDetails]
    
    M --> P[(Guardar en Postgres)]
    end