package com.mediguk.backend.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema; //Anotaciones para front end

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Regex mejor que invalida hibernate auto y no llega a service evitando use CPU
@Schema(description = "Datos para el registro de un nuevo paciente en Mediguk")
public record CreateUserDTO(
    @Schema(example = "12345678Z", description = "DNI o NIE del usuario")
    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "^[0-9]{8}[TRWAGMYFPDXBNJZSQVHLCKE]$|^[XYZ][0-9]{7}[TRWAGMYFPDXBNJZSQVHLCKE]$")
    String documentNumber,
    
    @Schema(example = "Aitor Menta", description = "Nombre completo")
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 3, max = 100)
    String fullName,
    
    @Schema(example = "aitor@mediguk.com")
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    String email,
    
    @Schema(example = "600123456")
    @Pattern(regexp = "^[679][0-9]{8}$", message = "El número debe ser un móvil español válido (9 dígitos)")
    String phoneNumber
) {}

// // Un método "extra" que no es un campo
//     public String fullNameUpperCase() {
//         return fullName.toUpperCase();
//     }
    
//     // Puedes incluso "sobrescribir" el getter por defecto
//     @Override
//     public String email() {
//         return email.toLowerCase().trim(); // Limpiamos el email siempre
//     }


// public CreateUserDTO {
//         if (!email.contains("@")) {
//             throw new IllegalArgumentException("¡Email inválido, hacker!");
//         }
//         // No necesitas poner "this.email = email", Java lo hace solo
//     }



//////////////////////////////////////////////////////////////////////////////////////
// import jakarta.validation.constraints.Email;
// import jakarta.validation.constraints.NotBlank;
// import lombok.Data;

// @Data for getter and setter 
// public class CreateUserDTO {

//   @NotBlank private String documentNumber;

//   @NotBlank private String fullName;

//   @Email @NotBlank private String email;

//   private String phoneNumber;
// }

//Record ya te da getters, equals(), hashcode(), toString(): NO hace falta @Data. NO HAY SETTERS. INMUTABLE. eWPTV2
