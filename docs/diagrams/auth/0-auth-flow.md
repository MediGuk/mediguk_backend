```mermaid
graph TD
    %% Registro/Login Initial
    A[<b>Usuario mete DNI</b>] --> B{¿DNI valido?}
    B -- OK --> C[Generar OTP + Guardar OTPHashed en DB]
    C --> D[Enviar OTP via Whatsapp/SMS/Email]
    
    %% Verificación y Entrega
    D --> E[Usuario envía OTP]
    E --> F{¿OTP Válido?}
    F -- SÍ --> G[<b>AUTORIZAR SESIÓN</b>]
    
    %% Respuesta HTTP (Corregido y Unificado)
    G --> H[Crear Registro 'AuthSession' en DB]
    H --> IRESPONSE["Enviar a cliente
    <b>HTTP 200 OK</b><br/>---
    <b>Headers (Cookies):</b><br/>refreshToken & fingerprint <br/>---
    <b>Body (JSON):</b><br/>JWT (sessionID + userId)"]
    
    %% Uso Diario
    IRESPONSE --> K[Client: Peticiones API
    Headers:
    <b>JWT</b> & <b>FingerPrintCookie</b>]
    K --> L{¿JWT Válido?}
    
    %% Control de Seguridad
    L -- SÍ --> M[Interceptor: 
    FingerprintCookie correcto?]
    M -- SÍ --> N[Procesar Petición OK]
    M -- NO --> P[<b>Alerta:</b> </br>Cerrar Sesión]
    
    %% Renovación
    L -- EXPIRADO --> Q[ <b>/refresh</b> route </br>RefreshCookie + FingerPrintCookie]
    Q --> R{¿Cookies refreshToken + Fingerprint válidas en DB?}
    R -- SÍ --> S[Rotar Tokens con nuevos: JWT + RefreshToken]
    S --> K
    R -- NO --> P

    %% Estilos limpios
    style G fill:#00c853,stroke:#000,color:#fff
    style IRESPONSE fill:#e3f2fd,stroke:#2196f3,stroke-width:2px,color:#000,text-align:center
    style P fill:#f44336,stroke:#000,color:#fff
    style N fill:#b9f6ca,stroke:#00c853,color:#000