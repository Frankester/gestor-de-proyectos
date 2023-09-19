package com.frankester.gestorDeProyectos.services;

import com.frankester.gestorDeProyectos.exceptions.custom.ProyectoNotFoundException;
import com.frankester.gestorDeProyectos.exceptions.custom.TareaNotFoundException;
import com.frankester.gestorDeProyectos.exceptions.custom.UsuarioNotFoundException;
import com.frankester.gestorDeProyectos.models.DTOs.TareaRequest;
import com.frankester.gestorDeProyectos.models.Tarea;
import com.frankester.gestorDeProyectos.models.Usuario;


public interface TareaService {

    public Tarea obtenerTareaConId(Long tareaId) throws TareaNotFoundException;

    public Tarea crearTarea(TareaRequest tareaRequest, Usuario usuarioDelCreadorDeLaTarea) throws UsuarioNotFoundException, ProyectoNotFoundException ;

    public Tarea actializarTarea(Long idTarea, TareaRequest tareaRequest, Usuario usuarioDelCreadorDeLaTarea) throws TareaNotFoundException, UsuarioNotFoundException, ProyectoNotFoundException ;

    public void guardarTareaModificada(Tarea tarea);
}
