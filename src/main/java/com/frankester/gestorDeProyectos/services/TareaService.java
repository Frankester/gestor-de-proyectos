package com.frankester.gestorDeProyectos.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Service
public class TareaService {

    @Autowired
    private RepoTareas repoTareas;

    @Autowired
    private UserService userService;

    @Autowired ProyectoService proyectoService;

    @Autowired
    private AmazonS3 s3;

    @Value("${aws.bucketName}")
    private String BucketName;

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

            try {
                persistirArchivo(file, tarea.getId());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        actualizarTarea(tarea);
    }

    public Tarea crearTarea(TareaRequest tareaRequest, Usuario usuarioDelCreadorDeLaTarea) throws UsuarioNotFoundException, ProyectoNotFoundException {

        Usuario usuarioAsignado = userService.obtnerUsuarioPorUsername(tareaRequest.getUsername());

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

        actualizarTarea(nuevaTarea);

        return nuevaTarea;
    }

    public Tarea actializarTarea(Long idTarea, TareaRequest tareaRequest, Usuario usuarioDelCreadorDeLaTarea) throws TareaNotFoundException, UsuarioNotFoundException, ProyectoNotFoundException {
        Usuario usuarioAsignado = this.userService.obtnerUsuarioPorUsername(tareaRequest.getUsername());

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

        actualizarTarea(tareaAActualizar);

        return tareaAActualizar;
    }

    private void persistirArchivo(MultipartFile file, Long tareaId) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());

        this.s3.putObject(BucketName, file.getName(),file.getInputStream(), metadata);
    }

}
