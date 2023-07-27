package com.frankester.gestorDeProyectos.models.DTOs;

import com.frankester.gestorDeProyectos.models.PanelDeControl;
import com.frankester.gestorDeProyectos.models.Tarea;
import com.frankester.gestorDeProyectos.models.Usuario;
import com.frankester.gestorDeProyectos.models.mensajeria.ChatRoom;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ProyectoRequest {

    @NotBlank(message = "Campo requrido")
    private String nombre;

    private String descripcion;

    @NotBlank(message = "Campo requrido")
    private LocalDate fechaLimite;

    @NotBlank(message = "Campo requrido")
    private List<String> miembros;

    private ChatRoomRequest salaDeChat;
}
