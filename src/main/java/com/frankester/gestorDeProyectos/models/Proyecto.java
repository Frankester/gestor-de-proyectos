package com.frankester.gestorDeProyectos.models;

import com.frankester.gestorDeProyectos.models.mensajeria.ChatRoom;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Proyecto extends Persistence {

    private String nombre;

    private String descripcion;

    private LocalDate fechaLimite;

    @OneToMany
    @JoinColumn(name = "id_proyecto", referencedColumnName =  "id")
    private List<Tarea> tareas;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "proyecto__usuario",
            joinColumns = @JoinColumn(name = "id_proyecto", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "username", referencedColumnName = "username")
    )
    private List<Usuario> miembros;

    @OneToOne
    @JoinColumn(name = "id_panel_de_control", referencedColumnName =  "id")
    private PanelDeControl panelDeControl;

    @OneToOne
    @JoinColumn(name = "id_chat_room", referencedColumnName =  "id")
    private ChatRoom salaDeChat;

    private Boolean proyectoVirgente;

    public Proyecto() {
        this.tareas = new ArrayList<>();
        this.miembros =  new ArrayList<>();
        this.proyectoVirgente = true;
    }
}