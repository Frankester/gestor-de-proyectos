package com.frankester.gestorDeProyectos.models;

import com.frankester.gestorDeProyectos.models.mensajeria.ChatRoom;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Usuario {

    @Id
    private String username;

    private String email;

    private String password;

    @ManyToMany(mappedBy = "miembros")
    private List<Proyecto> proyectos;


    @OneToMany(mappedBy = "usuairoAsignado")
    private List<Tarea> tareas;

    private Boolean cuenteVirgente;

    public Usuario() {
        this.proyectos = new ArrayList<>();
        this.tareas = new ArrayList<>();
        this.cuenteVirgente = true;
    }
}
