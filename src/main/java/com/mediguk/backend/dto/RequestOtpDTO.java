package com.mediguk.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data // getter & setter
public class RequestOtpDTO {

  @NotBlank private String documentNumber;
}
