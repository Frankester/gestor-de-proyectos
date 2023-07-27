package com.frankester.gestorDeProyectos.services;

import com.frankester.gestorDeProyectos.exceptions.custom.ProyectoNotFoundException;
import com.frankester.gestorDeProyectos.exceptions.custom.TareaNotFoundException;
import com.frankester.gestorDeProyectos.exceptions.custom.UsuarioNotFoundException;
import com.frankester.gestorDeProyectos.models.DTOs.TareaRequest;
import com.frankester.gestorDeProyectos.models.Proyecto;
import com.frankester.gestorDeProyectos.models.Tarea;
import com.frankester.gestorDeProyectos.models.Usuario;
import com.frankester.gestorDeProyectos.models.mensajeria.Mensaje;
import com.frankester.gestorDeProyectos.repositories.RepoTareas;
import com.frankester.gestorDeProyectos.repositories.RepoUsuarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

@Service
public class TareaService {

    @Autowired
    private RepoTareas repoTareas;

    @Autowired
    private UserService userService;

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

    public void actualizarTarea(Tarea tarea){
        this.repoTareas.save(tarea);
    }

    public void guardarArchivos(Map<String, MultipartFile> requestFiles, Tarea tarea){
        requestFiles.forEach((filename, file) -> {
            tarea.addArchivo(filename);

            persistirArchivo(file, tarea.getId());
        });

        actualizarTarea(tarea);
    }

    public Tarea crearTarea(TareaRequest tareaRequest, Usuario usuarioDelCreadorDeLaTarea) throws UsuarioNotFoundException {

        Usuario usuarioAsignado = userService.obtnerUsuarioPorUsername(tareaRequest.getUsername());

        Tarea nuevaTarea = new Tarea();
        nuevaTarea.setEstado(tareaRequest.getEstado());
        nuevaTarea.setDescripcion(tareaRequest.getDescripcion());
        nuevaTarea.setPrioridad(tareaRequest.getPrioridad());
        nuevaTarea.setFechaLimite(tareaRequest.getFechaLimite());
        nuevaTarea.setTitulo(tareaRequest.getTitulo());

        nuevaTarea.setUsuairoAsignado(usuarioAsignado);

        tareaRequest.getComentarios().forEach((mensaje -> {
            Mensaje comentario = new Mensaje();

            comentario.setMensaje(mensaje);
            comentario.setUsuario(usuarioDelCreadorDeLaTarea);

            nuevaTarea.addComentario(comentario);
        }));

        actualizarTarea(nuevaTarea);

        return nuevaTarea;
    }

    public Tarea actializarTarea(Long idTarea, TareaRequest tareaRequest, Usuario usuarioDelCreadorDeLaTarea) throws TareaNotFoundException, UsuarioNotFoundException {
        Usuario usuarioAsignado = this.userService.obtnerUsuarioPorUsername(tareaRequest.getUsername());

        Tarea tareaAActualizar = obtenerTareaConId(idTarea);
        tareaAActualizar.setEstado(tareaRequest.getEstado());
        tareaAActualizar.setDescripcion(tareaRequest.getDescripcion());
        tareaAActualizar.setPrioridad(tareaRequest.getPrioridad());
        tareaAActualizar.setFechaLimite(tareaRequest.getFechaLimite());
        tareaAActualizar.setTitulo(tareaRequest.getTitulo());

        tareaAActualizar.setUsuairoAsignado(usuarioAsignado);

        tareaRequest.getComentarios().forEach((mensaje -> {
            Mensaje comentario = new Mensaje();

            comentario.setMensaje(mensaje);
            comentario.setUsuario(usuarioDelCreadorDeLaTarea);

            tareaAActualizar.addComentario(comentario);
        }));

        actualizarTarea(tareaAActualizar);

        return tareaAActualizar;
    }

    private void persistirArchivo(MultipartFile file, Long tareaId){
        //TODO PERSISTIR ARCHIVO EN LA NUBE COMO AWS S3
    }

}
