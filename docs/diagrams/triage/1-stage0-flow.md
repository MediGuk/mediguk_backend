```mermaid
sequenceDiagram
    participant P as Paciente
    participant G as Go (Stage 0)
    participant S3 as AWS S3 / Cloud
    participant J as Java Service

    P->>G: Envía Imagen + "Me pica aquí"
    Note over G: Genera UUID V4 (Identidad única)
    G->>S3: Sube imagen original
    S3-->>G: URL de la imagen
    Note over G: Análisis rápido (Mini-VLM) para sugerir Categoría
    G->>J: POST /api/v1/triage/process (ID, PatientId, URL, Cat Sugerida)
    J-->>G: 200 OK (Recibido y en proceso)
    G-->>P: "Estamos analizando tu caso, espera..."