package com.frankester.gestorDeProyectos.models.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthDTO {

    @NotBlank(message = "Campo requerido")
    private String username;

    @NotBlank(message = "Campo requerido")
    private String password;

    @NotBlank(message = "Campo requerido")
    @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,}$", message = "El formato del email no es valido")
    private String email;


}
