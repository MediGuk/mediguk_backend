package com.mediguk.backend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyOtpDTO {

  @NotBlank private String documentNumber;

  @NotBlank private String otp;
}
