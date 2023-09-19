package com.frankester.gestorDeProyectos.services.impl;

import com.frankester.gestorDeProyectos.exceptions.custom.ProyectoNotFoundException;
import com.frankester.gestorDeProyectos.exceptions.custom.TareaNotFoundException;
import com.frankester.gestorDeProyectos.exceptions.custom.UsuarioNotFoundException;
import com.frankester.gestorDeProyectos.models.DTOs.TareaRequest;
import com.frankester.gestorDeProyectos.models.Proyecto;
import com.frankester.gestorDeProyectos.models.Tarea;
import com.frankester.gestorDeProyectos.models.Usuario;
import com.frankester.gestorDeProyectos.models.mensajeria.Mensaje;
import com.frankester.gestorDeProyectos.repositories.RepoTareas;
import com.frankester.gestorDeProyectos.services.ProyectoService;
import com.frankester.gestorDeProyectos.services.TareaService;
import com.frankester.gestorDeProyectos.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TareaServiceImpl implements TareaService {

    @Autowired
    private RepoTareas repoTareas;

    @Autowired
    private UsuarioService userService;

    @Autowired
    private ProyectoService proyectoService;

    @Override
    public Tarea obtenerTareaConId(Long tareaId) throws TareaNotFoundException {
        Optional<Tarea> tareaOp = this.repoTareas.findById(tareaId);

        if(tareaOp.isEmpty()){
            throw new TareaNotFoundException("No se encontro la tarea con id: " + tareaId);

        }
        Tarea tarea = tareaOp.get();

        if(!tarea.getTareaVirgente()){
            throw new TareaNotFoundException("No se encontro el proyecto con id: " + tareaId);
        }

        return tarea;
    }

    @Override
    public Tarea crearTarea(TareaRequest tareaRequest, Usuario usuarioDelCreadorDeLaTarea) throws UsuarioNotFoundException, ProyectoNotFoundException {

        Usuario usuarioAsignado = this.userService.obtenerUsuarioPorUsername(tareaRequest.getUsername());

        Proyecto proyecto = this.proyectoService.obtenerProyectoConId(tareaRequest.getIdProyecto());

        Tarea nuevaTarea = new Tarea();
        nuevaTarea.setEstado(tareaRequest.getEstado());
        nuevaTarea.setDescripcion(tareaRequest.getDescripcion());
        nuevaTarea.setPrioridad(tareaRequest.getPrioridad());
        nuevaTarea.setFechaLimite(tareaRequest.getFechaLimite());
        nuevaTarea.setTitulo(tareaRequest.getTitulo());

        nuevaTarea.setUsuairoAsignado(usuarioAsignado);
        nuevaTarea.setProyecto(proyecto);

        tareaRequest.getComentarios().forEach((mensaje -> {
            Mensaje comentario = new Mensaje();

            comentario.setMensaje(mensaje);
            comentario.setUsuario(usuarioDelCreadorDeLaTarea);

            nuevaTarea.addComentario(comentario);
        }));

        guardarTareaModificada(nuevaTarea);

        return nuevaTarea;
    }

    @Override
    public Tarea actializarTarea(Long idTarea, TareaRequest tareaRequest, Usuario usuarioDelCreadorDeLaTarea) throws TareaNotFoundException, UsuarioNotFoundException, ProyectoNotFoundException {
        Usuario usuarioAsignado = this.userService.obtenerUsuarioPorUsername(tareaRequest.getUsername());

        Proyecto proyecto = this.proyectoService.obtenerProyectoConId(tareaRequest.getIdProyecto());

        Tarea tareaAActualizar = obtenerTareaConId(idTarea);
        tareaAActualizar.setEstado(tareaRequest.getEstado());
        tareaAActualizar.setDescripcion(tareaRequest.getDescripcion());
        tareaAActualizar.setPrioridad(tareaRequest.getPrioridad());
        tareaAActualizar.setFechaLimite(tareaRequest.getFechaLimite());
        tareaAActualizar.setTitulo(tareaRequest.getTitulo());

        tareaAActualizar.setUsuairoAsignado(usuarioAsignado);
        tareaAActualizar.setProyecto(proyecto);

        tareaRequest.getComentarios().forEach((mensaje -> {
            Mensaje comentario = new Mensaje();

            comentario.setMensaje(mensaje);
            comentario.setUsuario(usuarioDelCreadorDeLaTarea);

            tareaAActualizar.addComentario(comentario);
        }));

        guardarTareaModificada(tareaAActualizar);

        return tareaAActualizar;
    }


    public void guardarTareaModificada(Tarea tarea){
        this.repoTareas.save(tarea);
    }

}
