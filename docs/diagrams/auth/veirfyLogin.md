

```mermaid
sequenceDiagram
    autonumber
    participant C as Cliente (Frontend)
    participant API as AuthController
    participant S as AuthService
    participant OS as OtpService
    participant SS as SessionService
    participant R as Repositories
    participant JWT as JwtService

    Note over C, JWT: POST /auth/verify-otp { documentNumber, otp }

    C->>API: Enviar credenciales
    API->>S: verifyLogin(dto, request)
    
    S->>OS: validateOtp(user, otpRequest)
    
    Note over OS, R: Rate Limit & Security Check
    OS->>R: findActiveOtp(user, now)
    OS->>R: countRecentAttempts(user)
    
    alt ⚠️ Demasiados intentos o OTP inválido
        OS-->>C: 429 Too many attempts / 401 Invalid OTP
    end

    Note over OS: ✅ OTP Correcto: otp.used = true

    rect rgb(45, 45, 45)
        Note over S, SS: Generación de Sesión Segura
        S->>SS: createSession(user, request)
        SS->>R: Save AuthSession (IP, UA, FingerprintHash)
        SS-->>S: Session + Fingerprint (Plain)
    end

    S->>JWT: generateToken(userId, sessionToken)
    S-->>API: AuthResult { accessToken, refreshToken, fingerprint }

    Note over API: Cookie Security: HttpOnly, Secure, Strict
    API->>C: 200 OK + Body (AuthResult)