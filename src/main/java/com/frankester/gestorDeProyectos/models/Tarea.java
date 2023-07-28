package com.frankester.gestorDeProyectos.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.frankester.gestorDeProyectos.models.estados.EstadoTarea;
import com.frankester.gestorDeProyectos.models.estados.Prioridad;
import com.frankester.gestorDeProyectos.models.mensajeria.Mensaje;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
public class Tarea extends Persistence{

    private String titulo;

    private String descripcion;

    private LocalDate fechaLimite;

    @Enumerated(EnumType.STRING)
    private Prioridad prioridad;

    @Enumerated(EnumType.STRING)
    private EstadoTarea estado;

    @ElementCollection
    @CollectionTable(name = "tarea__archivo",
            joinColumns = @JoinColumn(name = "id_tarea", referencedColumnName = "id"))
    @Column(name = "archivo")
    private Set<String> archivos;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tarea", referencedColumnName =  "id")
    private List<Mensaje> comentarios;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_asignado", referencedColumnName =  "username")
    private Usuario usuairoAsignado;

    @JsonIgnore
    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name = "id_proyecto", referencedColumnName =  "id")
    private Proyecto proyecto;

    private Boolean tareaVirgente;

    public Tarea() {
        this.archivos = new HashSet<>();
        this.comentarios = new ArrayList<>();
        this.tareaVirgente = true;
    }

    public void addComentario(Mensaje comentario){
        this.comentarios.add(comentario);
    }

    public void addArchivo(String nombreArchivo){
        this.archivos.add(nombreArchivo);
    }
}
