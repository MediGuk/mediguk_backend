# OTP request and generation

Este flujo describe la lógica de negocio para solicitar un código de un solo uso (OTP) garantizando seguridad mediante hashing y control de abuso (Rate Limiting).

```mermaid
sequenceDiagram
    autonumber
    participant C as Cliente (App/Web)
    participant API as AuthController
    participant S as AuthService
    participant R as OtpRepository

    Note over C, R: POST /auth/request-otp { documentNumber }

    C->>API: Enviar datos
    API->>S: requestOtp(dto)
    S->>R: findByDocumentNumber()

    alt ❌ Usuario NO existe
        S-->>C: 404 Not Found
    else ✅ Usuario Existe
        S->>R: findTopByUserOrderByExpiresAtDesc()
        
        alt ⚠️ Rate Limit activo (< 60s)
            Note right of S: lastSentAt + 60s > now
            S-->>C: 429 Too Many Requests
        
        else 🔄 Reenviar OTP existente (> 60s)
            S->>R: update lastSentAt = now
            S-->>C: 200 OK (OTP reenviado)

        else ✨ Generar Nuevo (Expirado o usado)
            S->>R: invalidateOtpsForUser(user)
            Note right of R: UPDATE used = true WHERE user_id = ?
            
            rect rgb(45, 45, 45)
                Note over S: 🔒 Proceso de Seguridad
                S->>S: code = SecureRandom (6 digits)
                S->>S: hashedCode = passwordEncoder.encode(code)
            end

            S->>R: save(OtpEntity)
            Note right of R: code: hashedCode, expiresAt: +5m,<br/>attempts: 0, used: false
            S-->>C: 200 OK (Nuevo OTP generado)
        end
    end

    Note over S, R: 🕒 OtpCleanupService (Scheduler)<br/>DELETE FROM otps WHERE expiresAt < now