// CONTROLLER HTTP RELATED ONES ALWAYS HERE
package com.mediguk.backend.controller;

import com.mediguk.backend.dto.RequestOtpDTO;
import com.mediguk.backend.dto.VerifyOtpDTO;
import com.mediguk.backend.model.AuthResult;
import com.mediguk.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/request-otp")
  public void requestOtp(@RequestBody RequestOtpDTO dto) {
    authService.requestOtp(dto);
  }

  @PostMapping("/verify-otp")
  public ResponseEntity<Map<String, String>> verifyOtp(
      @RequestBody VerifyOtpDTO dto, HttpServletRequest request, HttpServletResponse response) {

    // 1. Verify OTP and get result
    AuthResult result = authService.verifyOtp(dto, request);

    // 2. Get fingerprint from result
    String fingerprint = result.fingerprint();

    // 3. Create cookie of fingerprint
    ResponseCookie cookie =
        ResponseCookie.from("fingerprint", fingerprint)
            .httpOnly(true) // JS devtools cannot read cookies (XSS protection)
            .secure(true) // only on HTTPS
            .path("/") // cookie send to all the API
            .maxAge(60 * 60 * 24 * 30) // 30 days of duration
            .sameSite("Strict") // (CSRF protection)
            .build();

    // 4. Send HTPP responde
    return ResponseEntity.ok()
        .header("Set-Cookie", cookie.toString()) // navegator saves it automatically (cookie)
        .body(Map.of("accessToken", result.accessToken()));
  }
}
