package com.frankester.gestorDeProyectos.models;

import com.frankester.gestorDeProyectos.models.estados.EstadoTarea;
import com.frankester.gestorDeProyectos.models.estados.Prioridad;
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

    @OneToMany(mappedBy = "proyecto")
    private List<Tarea> tareas;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "proyecto__usuario",
            joinColumns = @JoinColumn(name = "id_proyecto", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "username", referencedColumnName = "username")
    )
    private List<Usuario> miembros;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_panel_de_control", referencedColumnName =  "id")
    private PanelDeControl panelDeControl;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_chat_room", referencedColumnName =  "id")
    private ChatRoom salaDeChat;

    private Boolean proyectoVirgente;

    public Proyecto() {
        this.tareas = new ArrayList<>();
        this.miembros =  new ArrayList<>();
        this.proyectoVirgente = true;
    }

    public void addMiembro(Usuario usuario){
        this.miembros.add(usuario);
    }

    public void addTarea(Tarea tarea){
        this.tareas.add(tarea);
    }

    public Float calcularProgresoDelProyecto(){
        int cantidadDeTareasTotal = this.tareas.size();
        int cantidadDeTareasRealizadas = this.tareas.stream()
                .filter(tarea -> tarea.getEstado().equals(EstadoTarea.REALIZADA) )
                .toList()
                .size();

        return (float) ((cantidadDeTareasRealizadas/ cantidadDeTareasTotal)* 100);
    }

    public List<Tarea> obtenerAlertasDelProyecto(){
        return this.tareas.stream()
                .filter(tarea -> tarea.getEstado().equals(EstadoTarea.PENDIENTE) && tarea.getPrioridad().equals(Prioridad.ALTA)  )
                .toList();
    }

    public List<Tarea> obtenerTareasPendientes(){
        return this.tareas.stream()
                .filter(tarea -> tarea.getEstado().equals(EstadoTarea.PENDIENTE))
                .toList();
    }


}