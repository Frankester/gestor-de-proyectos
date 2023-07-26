package com.frankester.gestorDeProyectos.models.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VerificationCodeRequest {

    @NotBlank(message = "Campo requerido")
    private String email;

    @NotBlank(message = "Campo requerido")
    private String verificationCode;
}
