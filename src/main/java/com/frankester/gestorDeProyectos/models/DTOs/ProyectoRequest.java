package com.frankester.gestorDeProyectos.models.DTOs;

import com.frankester.gestorDeProyectos.models.PanelDeControl;
import com.frankester.gestorDeProyectos.models.Tarea;
import com.frankester.gestorDeProyectos.models.Usuario;
import com.frankester.gestorDeProyectos.models.mensajeria.ChatRoom;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
public class ProyectoRequest {

    @NotBlank(message = "Campo requerido")
    private String nombre;

    private String descripcion;

    @NotNull(message = "Campo requerido")
    @Future(message = "La fecha limite no puede estar en el pasado o presente")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaLimite;

    @NotNull(message = "Campo requerido")
    @Size(min = 1, message = "Se requiere al menos un miembro para el proyecto")
    private List<@NotBlank(message = "Es necesario indicar el username del miembro") String> miembros;

    @NotNull(message = "Campo requerido")
    @Valid
    private ChatRoomRequest salaDeChat;
}
