package com.frankester.gestorDeProyectos.models.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthDTO {

    @NotBlank(message = "Campo requerido")
    private String username;

    @NotBlank(message = "Campo requerido")
    private String password;


}
