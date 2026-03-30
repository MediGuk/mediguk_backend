// CONTROLLER HTTP RELATED ONES ALWAYS HERE
package com.mediguk.backend.auth.controller;

import com.mediguk.backend.auth.dto.request.RequestOtpDTO;
import com.mediguk.backend.auth.dto.request.VerifyOtpDTO;
import com.mediguk.backend.auth.dto.response.AuthResponse;
import com.mediguk.backend.auth.model.AuthResult;
import com.mediguk.backend.auth.service.AuthService;
import com.mediguk.backend.auth.service.JwtService;
import org.springframework.beans.factory.annotation.Value;

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

  @Value("${app.security.cookie.secure:true}")
  private boolean cookieSecure;

  @Value("${app.security.cookie.samesite:Strict}")
  private String cookieSameSite;

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/request-otp")
  public ResponseEntity<Map<String, String>> requestOtp(@RequestBody RequestOtpDTO dto) {
    String otp = authService.requestLogin(dto);
    // For the DEMO
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
    ResponseCookie cookie = ResponseCookie.from("fingerprint", fingerprint)
        .httpOnly(true) // JS devtools cannot read cookies (XSS protection)
        .secure(cookieSecure) // only on HTTPS/local-env-prop
        .path("/") // Send cookie to all the API
        .maxAge(60 * 60 * 24 * 30) // 30 days of duration
        .sameSite(cookieSameSite) // CSRF protection
        .build();

    // 4. Create cookie of refreshToken
    ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
        .httpOnly(true)
        .secure(cookieSecure)
        .path("/")
        .maxAge(60 * 60 * 24 * 30)
        .sameSite(cookieSameSite)
        .build();

    // 5. Create headers and ADD all cookies (to dont overwrite)
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
    headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());

    // 6. Send HTPP responde
    return ResponseEntity.ok()
        .headers(headers)
        .body(new AuthResponse(result.jwtToken(), jwtService.getExpirationTime()));
  }

  @PostMapping("/logout")
  public ResponseEntity<Map<String, String>> logout(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
    String token = authHeader.substring(7);
    authService.logout(token);

    // Borramos la cookie de la cara del cliente
    ResponseCookie deleteCookie = ResponseCookie.from("fingerprint", "")
        .maxAge(0) // Expired 0s ago, DELETE IT
        .path("/")
        .httpOnly(true)
        .secure(cookieSecure)
        .sameSite(cookieSameSite)
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
    ResponseCookie newRefreshCookie = ResponseCookie.from("refreshToken", result.refreshToken())
        .httpOnly(true)
        .secure(cookieSecure)
        .path("/auth/refresh")
        .maxAge(60 * 60 * 24 * 30)
        .sameSite(cookieSameSite)
        .build();

    // 3. Respond
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, newRefreshCookie.toString())
        .body(new AuthResponse(result.jwtToken(), jwtService.getExpirationTime()));
  }
}
