

```mermaid
sequenceDiagram
    autonumber
    participant C as Cliente (Frontend)
    participant API as AuthController
    participant S as AuthService
    participant SS as SessionService
    participant R as Repositories (OTP/Session)
    participant JWT as JwtService

    Note over C, JWT: POST /auth/verify-otp { documentNumber, otp }

    C->>API: Enviar credenciales
    API->>S: verifyOtp(dto, request)
    
    S->>R: findByDocumentNumber()
    alt ❌ Usuario no existe
        S-->>C: 404 User Not Found
    end

    S->>R: findActiveOtp(user, now)
    alt ❌ OTP inválido/expirado/usado
        S-->>C: 400 OTP invalid or expired
    end

    Note over S, R: Rate Limit & Attempts Check
    S->>R: countRecentAttempts(user)
    alt ⚠️ Demasiados intentos (>10 total o >5 este OTP)
        S-->>C: 429 Too many attempts
    end

    S->>S: passwordEncoder.matches(otp, hashedOtp)
    alt ❌ OTP Incorrecto
        S->>R: increment otp.attempts
        S-->>C: 401 Invalid OTP
    end

    Note over S: ✅ OTP Correcto: otp.used = true

    rect rgb(45, 45, 45)
        Note over S, SS: Generación de Sesión Segura
        S->>SS: createSession(user, request)
        SS->>SS: SecureRandom Fingerprint
        SS->>SS: Hash Fingerprint (BCrypt)
        SS->>R: Save AuthSession (UUID, IP, UA, FingerprintHash)
        SS-->>S: Session + Fingerprint (Plain)
    end

    S->>JWT: generateToken(userId, sessionToken)
    JWT-->>S: accessToken (JWT)

    S-->>API: AuthResult { accessToken, fingerprint }

    Note over API: 🍪 Cookie Security: HttpOnly, Secure, Strict
    API->>C: 200 OK + Set-Cookie (fingerprint) + Body (accessToken)

    Note over C: El navegador guarda el Fingerprint<br/>y el App el JWT