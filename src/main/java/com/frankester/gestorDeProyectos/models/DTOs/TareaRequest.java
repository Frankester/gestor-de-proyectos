package com.frankester.gestorDeProyectos.models.DTOs;

import com.frankester.gestorDeProyectos.models.estados.EstadoTarea;
import com.frankester.gestorDeProyectos.models.estados.Prioridad;
import com.frankester.gestorDeProyectos.models.mensajeria.MensajeDeChat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TareaRequest {

    @NotBlank(message = "Campo requerido")
    private String titulo;

    private String descripcion;

    @NotBlank(message = "Campo requerido")
    private LocalDate fechaLimite;

    @NotBlank(message = "Campo requerido")
    private Prioridad prioridad;

    @NotBlank(message = "Campo requerido")
    private EstadoTarea estado;

    private List<String> comentarios;

    @NotBlank(message = "Campo requerido")
    private String username;

    @NotBlank(message = "Campo requerido")
    private Long idProyecto;

}
