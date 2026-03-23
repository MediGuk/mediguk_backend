package com.mediguk.backend.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record VerifyOtpDTO (

  @Schema(example = "12345678Z", description = "DNI o NIE del usuario")
  @NotBlank(message = "El DNI es obligatorio")
  @Pattern(regexp = "^[0-9]{8}[TRWAGMYFPDXBNJZSQVHLCKE]$|^[XYZ][0-9]{7}[TRWAGMYFPDXBNJZSQVHLCKE]$")
  @NotBlank 
  String documentNumber,

  @NotBlank(message = "Campo obligatorio")
  @Size(min = 4, max = 4, message = "El código debe tener exactamente 4 dígitos")
  @Pattern(regexp = "^[0-9]*$", message = "El OTP debe ser numérico")
  @Schema( description = "Código de verificación de 4 dígitos", example = "1234")
  String otp
) {}
