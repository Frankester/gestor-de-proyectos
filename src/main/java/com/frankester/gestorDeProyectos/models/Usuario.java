package com.frankester.gestorDeProyectos.models;

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

}
