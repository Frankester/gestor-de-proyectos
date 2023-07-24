package com.frankester.gestorDeProyectos.models;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class PanelDeControl extends Persistence{

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_panel_de_control", referencedColumnName =  "id")
    private List<Tarea> tareasPendientes;

    private Float progresoDelProyecto;

    @OneToMany
    @JoinColumn(name = "id_panel_de_control", referencedColumnName =  "id")
    private List<Tarea> alertas;

    public PanelDeControl() {
        this.tareasPendientes = new ArrayList<>();
        this.alertas = new ArrayList<>();
    }
}
