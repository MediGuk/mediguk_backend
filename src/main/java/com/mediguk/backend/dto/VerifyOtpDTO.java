package com.mediguk.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyOtpDTO {

  @NotBlank private String documentNumber;

  @NotBlank private String otp;
}
