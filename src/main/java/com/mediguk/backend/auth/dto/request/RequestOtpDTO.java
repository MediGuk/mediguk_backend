package com.mediguk.backend.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema; //Anotaciones para front end
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RequestOtpDTO (
    @Schema(example = "12345678Z", description = "DNI o NIE del usuario")
    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "^[0-9]{8}[TRWAGMYFPDXBNJZSQVHLCKE]$|^[XYZ][0-9]{7}[TRWAGMYFPDXBNJZSQVHLCKE]$")
    @NotBlank 
    String documentNumber
) {} //ClassBody para methods




