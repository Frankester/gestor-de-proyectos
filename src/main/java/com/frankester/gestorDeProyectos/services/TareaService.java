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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
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
    private S3Client s3;

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

    public void guardarArchivos(Map<String, MultipartFile> requestFiles, Tarea tarea) throws IOException {

        for(MultipartFile file: requestFiles.values()){
            String fileName = file.getOriginalFilename();

            tarea.addArchivo(fileName);

            persistirArchivo(file, tarea.getId());
        }

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

    public byte[] descargarAchivo(String filename,Tarea tarea) throws IOException {
        if(!existeArchivoConNombre(filename)){
            throw new FileNotFoundException("No existe el archivo con el nombre '" + filename +"'");
        }

        boolean esArchivoDeLaTarea = tarea.getArchivos().contains(filename);

        if(!esArchivoDeLaTarea){
            throw new FileNotFoundException("El archivo '" + filename +"' no pertenece a la tarea "+ tarea.getTitulo());
        }

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(this.BucketName)
                .key(filename)
                .build();

        ResponseInputStream<GetObjectResponse> getObjectResponse = s3.getObject(getObjectRequest);

        return getObjectResponse.readAllBytes();
    }

    private void persistirArchivo(MultipartFile file, Long tareaId) throws IOException {
        String fileName = file.getOriginalFilename();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(this.BucketName)
                .key(fileName)
                .build();

        this.s3.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
    }

    private boolean existeArchivoConNombre(String filename){
        try{
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .key(filename)
                    .bucket(this.BucketName)
                    .build();

            this.s3.headObject(headObjectRequest);

            return true;
        } catch(S3Exception exception){
            if(exception.statusCode() == 404){
                return false;
            }
        }
        return false;
    }

}
