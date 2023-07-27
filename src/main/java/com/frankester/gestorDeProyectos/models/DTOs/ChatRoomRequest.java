package com.frankester.gestorDeProyectos.models.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRoomRequest {

    @NotBlank(message = "Campo requerido")
    private String nombre;

    @NotBlank(message = "Campo requerido")
    private String adminUsername;
}
