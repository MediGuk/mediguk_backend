```mermaid
sequenceDiagram
    participant Client as Cliente (Navegador)
    participant Filter as JwtAuthenticationFilter
    participant DB as AuthSessionRepository
    participant Security as SecurityContextHolder

    Note over Client, Filter: Request con Header 'Bearer JWT' y Cookie 'fingerprint'
    
    Client->>Filter: HTTP Request
    
    alt ¿Tiene Header Authorization?
        Filter->>Filter: Extraer Token y Claims (userId, sessionToken)
    else No tiene Header
        Filter->>Client: 403 Forbidden (vía Spring Security)
    end

    Filter->>DB: findBySessionTokenAndRevokedFalse(sessionToken)
    
    alt Sesión encontrada en DB
        DB-->>Filter: Session Object (con FingerprintHash)
    else Sesión revocada o no existe
        Filter->>Client: Detener cadena (Acceso denegado)
    end

    alt ¿Tiene Cookie 'fingerprint'?
        Filter->>Filter: Comparar Cookie con Hash (BCrypt.matches)
        
        alt Fingerprint Válido
            Filter->>Security: setAuthentication(userId, roles)
            Note right of Security: Usuario marcado como AUTENTICADO
            Filter->>Filter: filterChain.doFilter()
        else Fingerprint Inválido
            Filter->>Client: Detener cadena
        end
        
    else Sin Cookie
        Filter->>Client: Detener cadena
    end