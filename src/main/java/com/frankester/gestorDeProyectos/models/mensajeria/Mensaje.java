package com.frankester.gestorDeProyectos.models.mensajeria;

import com.frankester.gestorDeProyectos.models.Persistence;
import com.frankester.gestorDeProyectos.models.Usuario;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Mensaje extends Persistence {

    private String mensaje;

    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username")
    private Usuario usuario;
}
