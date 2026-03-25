// CONTROLLER HTTP RELATED ONES ALWAYS HERE
package com.mediguk.backend.auth.controller;

import com.mediguk.backend.auth.dto.request.RequestOtpDTO;
import com.mediguk.backend.auth.dto.request.VerifyOtpDTO;
import com.mediguk.backend.auth.dto.response.AuthResponse;
import com.mediguk.backend.auth.model.AuthResult;
import com.mediguk.backend.auth.service.AuthService;
import com.mediguk.backend.auth.service.JwtService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "Endpoints para el manejo de OTP y sesiones")
public class AuthController {

  @Autowired
  private JwtService jwtService;

  private final AuthService authService;

  public AuthController(AuthService authService, Long jwtExpiration) {
    this.authService = authService;
  }

  @PostMapping("/request-otp")
  public ResponseEntity<Map<String, String>> requestOtp(@RequestBody RequestOtpDTO dto) {
    String otp = authService.requestLogin(dto);
    //For the DEMO
    // 5. Send HTPP responde
    return ResponseEntity.ok()
            .body(Map.of("otp", otp));    
  }

  @PostMapping("/verify-otp")
  @Operation(summary = "Verificar código OTP", description = "Valida el código enviado al celular/correo del usuario")
  public ResponseEntity<AuthResponse> verifyOtp(
      @RequestBody VerifyOtpDTO dto, HttpServletRequest request, HttpServletResponse response) {

    // 1. Verify OTP and get result
    AuthResult result = authService.verifyLogin(dto, request);

    // 2. Get fingerprint & refreshToken from result
    String fingerprint = result.fingerprintRaw();
    String refreshToken = result.refreshToken();

    // 3. Create cookie of fingerprint
    ResponseCookie cookie =
        ResponseCookie.from("fingerprint", fingerprint)
            .httpOnly(true) // JS devtools cannot read cookies (XSS protection)
            .secure(true) // only on HTTPS
            .path("/") // cookie send to all the API
            .maxAge(60 * 60 * 24 * 30) // 30 days of duration
            .sameSite("Strict") // (CSRF protection)
            .build();

    // 4. Create cookie of refreshToken
    ResponseCookie refreshCookie =
        ResponseCookie.from("refreshToken", refreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/") // Ojo: ponlo en "/" para que llegue al endpoint de refresh
            .maxAge(60 * 60 * 24 * 30)
            .sameSite("Strict")
            .build();

    // 5. Send HTPP responde
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString()) // navegator saves cookie automatically
        .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
        .body(new AuthResponse (result.jwtToken(), jwtService.getExpirationTime()));
  }

  @PostMapping("/logout")
  public ResponseEntity<Map<String, String>> logout(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
    String token = authHeader.substring(7);
    authService.logout(token);

    // Borramos la cookie de la cara del cliente
    ResponseCookie deleteCookie =
        ResponseCookie.from("fingerprint", "")
            .maxAge(0) // Expired 0s ago, DELETE IT
            .path("/")
            .httpOnly(true)
            .secure(true)
            .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
        .body(Map.of("status", "Logged out successfully"));
  }

  @PostMapping("/refresh")
  public ResponseEntity<AuthResponse> refresh(
      @CookieValue(name = "refreshToken") String oldRefreshToken,
      @CookieValue(name = "fingerprint") String fingerprintRaw) {
    // 1. Refresh JWT & refreshtoken + rawFingerPrint
    AuthResult result = authService.refresh(oldRefreshToken, fingerprintRaw);

    // 2. Create response cookie
    ResponseCookie newRefreshCookie =
        ResponseCookie.from("refreshToken", result.refreshToken())
            .httpOnly(true)
            .secure(true)
            .path("/auth/refresh")
            .maxAge(60 * 60 * 24 * 30)
            .sameSite("Strict")
            .build();

    // 3. Respond
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, newRefreshCookie.toString())
        .body(new AuthResponse(result.jwtToken(), jwtService.getExpirationTime()));
  }
}
