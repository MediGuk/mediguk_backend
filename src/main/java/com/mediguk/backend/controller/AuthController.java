package com.mediguk.backend.controller;

import com.mediguk.backend.dto.RequestOtpDTO;
import com.mediguk.backend.dto.VerifyOtpDTO;
import com.mediguk.backend.service.AuthService;
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
  public void verifyOtp(@RequestBody VerifyOtpDTO dto) {
    authService.verifyOtp(dto);
  }
}
