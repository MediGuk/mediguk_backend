package com.mediguk.backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUserDTO {

  @NotBlank private String documentNumber;

  @NotBlank private String fullName;

  @Email @NotBlank private String email;

  private String phoneNumber;
}
