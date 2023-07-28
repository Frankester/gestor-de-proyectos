package com.frankester.gestorDeProyectos.models.DTOs;

import com.frankester.gestorDeProyectos.models.estados.EstadoTarea;
import com.frankester.gestorDeProyectos.models.estados.Prioridad;
import com.frankester.gestorDeProyectos.models.mensajeria.MensajeDeChat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
public class TareaRequest {

    @NotBlank(message = "Campo requerido")
    private String titulo;

    private String descripcion;

    @NotNull(message = "Campo requerido")
    @Future(message = "La fecha limite no puede estar en el pasado o presente")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaLimite;

    @NotNull(message = "Campo requerido")
    private Prioridad prioridad;

    @NotNull(message = "Campo requerido")
    private EstadoTarea estado;

    private List<@NotBlank(message = "No puede haber un comentario vacio") String> comentarios;

    @NotBlank(message = "Campo requerido")
    private String username;

    @NotNull(message = "Campo requerido")
    private Long idProyecto;

}
