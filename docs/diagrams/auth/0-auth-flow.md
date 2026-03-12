```mermaid
graph TD
    %% Registro/Login Initial
    A[Inicio: Usuario mete DNI] --> B{¿DNI válido?}
    B -- SÍ --> C[Generar OTP + Hash en DB]
    C --> D[Enviar SMS/Email al Usuario]
    
    %% Verificación
    D --> E[Usuario mete OTP]
    E --> F{¿OTP == Hash DB?}
    F -- SÍ --> G[<b>LOGIN EXITOSO</b>]
    
    %% Entrega de Llaves
    G --> H[Generar Session en DB]
    H --> I[Entregar JWT al Body]
    H --> J[Entregar Fingerprint + RefreshToken en Cookies]
    
    %% Uso Diario
    I --> K[Peticiones API con JWT]
    K --> L{¿JWT expirado?}
    
    %% Ciclo de Vida
    L -- NO --> K
    L -- SÍ --> M[Llamada automática a /REFRESH]
    
    %% Renovación
    M --> N{¿RefreshToken y Fingerprint OK?}
    N -- SÍ --> O[Rotar RefreshToken + Nuevo JWT]
    O --> K
    N -- NO --> P[Sesión Cerrada: Volver a inicio]

    %% Estilos
    style G fill:#2d5,stroke:#333,stroke-width:2px
    style J fill:#f96,stroke:#333,dash
    style O fill:#3498db,stroke:#fff