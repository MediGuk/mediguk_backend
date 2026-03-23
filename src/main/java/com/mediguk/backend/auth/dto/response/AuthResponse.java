package com.mediguk.backend.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta tras un login o registro exitoso con el token de acceso")
public record AuthResponse(
    @Schema(description = "Token JWT para autenticar las peticiones (Bearer Token)",example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    String jwtToken,

    @Schema(description = "Tipo de token", example = "Bearer")
    String tokenType,

    @Schema(description = "Tiempo de expiración en milisegundos", example = "3600000")
    Long expiresIn
) 
{
    // Constructor compacto para poner valores por defecto
    public AuthResponse(String jwtToken, Long expiresIn) {
        this(jwtToken, "Bearer", expiresIn);
    }
}
