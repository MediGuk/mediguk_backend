package com.mediguk.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUserDTO {

  @Email @NotBlank private String email;

  @NotBlank private String password;

  private String phoneNumber;
}
