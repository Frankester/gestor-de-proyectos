package com.frankester.gestorDeProyectos.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.frankester.gestorDeProyectos.models.mensajeria.ChatRoom;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Usuario {

    @Id
    private String username;

    private String email;

    @JsonIgnore
    private String password;

    @JsonIgnore
    @ManyToMany(mappedBy = "miembros")
    private List<Proyecto> proyectos;

    @JsonIgnore
    @OneToMany(mappedBy = "usuairoAsignado")
    private List<Tarea> tareas;

    private Boolean cuenteVirgente;

    @JsonIgnore
    private String verificationCode;

    @JsonIgnore
    private LocalDateTime verificationCodeExpiration;

    private Boolean isEmailVerificated;

    @JsonIgnore
    private Integer verificationCodeTries;

    public Usuario() {
        this.proyectos = new ArrayList<>();
        this.tareas = new ArrayList<>();
        this.cuenteVirgente = true;
        this.isEmailVerificated = false;
        this.verificationCodeTries = 0;
    }
}
