# OTP request and generation

Este flujo describe la lógica de negocio para solicitar un código de un solo uso (OTP) garantizando seguridad mediante hashing y control de abuso (Rate Limiting).

```mermaid
sequenceDiagram
    autonumber
    participant C as Cliente (App/Web)
    participant API as AuthController
    participant S as AuthService
    participant US as UserService
    participant OS as OtpService
    participant R as OtpRepository

    Note over C, R: POST /auth/request-otp { documentNumber }

    C->>API: Enviar datos
    API->>S: requestLogin(dto)
    S->>US: getUserByDocument(dni)
    US->>R: findByDocumentNumber()

    alt ❌ Usuario NO existe
        US-->>C: 404 User not found
    else ✅ Usuario Existe
        S->>OS: requestOtp(user)
        OS->>R: findTopByUserOrderByExpiresAtDesc()
        
        alt ⚠️ Rate Limit activo (< 60s)
            Note right of OS: lastSentAt + 60s > now
            OS-->>C: 429 Please wait X seconds
        
        else ✨ Generar Nuevo (Siempre crea uno limpio)
            OS->>R: invalidateOtpsForUser(user)
            
            rect rgb(45, 45, 45)
                Note over OS: 🔒 Proceso de Seguridad
                OS->>OS: code = SecureRandom (4-6 digits)
                OS->>OS: hashedCode = passwordEncoder.encode(code)
            end

            OS->>R: save(OtpEntity)
            Note right of R: code: hashedCode, expiresAt: +5m,<br/>attempts: 0, used: false
            OS-->>C: 200 OK (OTP generado)
        end
    end

    Note over S, R: 🕒 OtpCleanupService (Scheduler)<br/>DELETE FROM otps WHERE expiresAt < now