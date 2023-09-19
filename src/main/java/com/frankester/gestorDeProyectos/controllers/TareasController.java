package com.frankester.gestorDeProyectos.controllers;

import com.frankester.gestorDeProyectos.exceptions.custom.ProyectoNotFoundException;
import com.frankester.gestorDeProyectos.exceptions.custom.TareaNotFoundException;
import com.frankester.gestorDeProyectos.exceptions.custom.UsuarioNotFoundException;
import com.frankester.gestorDeProyectos.models.DTOs.TareaRequest;
import com.frankester.gestorDeProyectos.models.Tarea;
import com.frankester.gestorDeProyectos.models.Usuario;
import com.frankester.gestorDeProyectos.services.ArchivosService;
import com.frankester.gestorDeProyectos.services.TareaService;
import com.frankester.gestorDeProyectos.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RepositoryRestController(path = "/tareas")
public class TareasController {


    @Autowired
    public TareaService tareaService;

    @Autowired
    public UsuarioService userService;

    @Autowired
    public ArchivosService archivosService;

    @DeleteMapping("/{idTarea}")
    public ResponseEntity<?> borrarTarea(@PathVariable Long idTarea) throws TareaNotFoundException {
        Tarea tarea = this.tareaService.obtenerTareaConId(idTarea);

        tarea.setTareaVirgente(false);

        this.tareaService.guardarTareaModificada(tarea);

        return ResponseEntity.ok("Se elimino la tarea " + tarea.getTitulo() + " correctamente. para el proyecto '" +tarea.getProyecto().getNombre()+ "'");

    }

    @PostMapping("/{idTarea}/archivos")
    public ResponseEntity<?> guardarArchivoNuevo(@PathVariable Long idTarea, @RequestParam("file") Map<String, MultipartFile> requestFiles) throws TareaNotFoundException, IOException {

        Tarea tarea = this.tareaService.obtenerTareaConId(idTarea);

        this.archivosService.guardarArchivos(requestFiles, tarea);


        return ResponseEntity.ok("Se agregaron " +requestFiles.size()+ " archivos para la tarea '" +tarea.getTitulo()+ "' con exito");
    }

    @GetMapping("/{idTarea}/archivos")
    public ResponseEntity<?> descargarArchivo(@PathVariable Long idTarea, @RequestParam String filename) throws TareaNotFoundException, IOException {

        Tarea tarea = this.tareaService.obtenerTareaConId(idTarea);

        byte[] fileToDownload = this.archivosService.descargarAchivo(filename, tarea);

        Resource fileResource = new ByteArrayResource(fileToDownload);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+filename+"\"")
                .body(fileResource);
    }

    @PostMapping(path = {"/", ""})
    public ResponseEntity<?> crearTarea(@Valid  @RequestBody TareaRequest request) throws UsuarioNotFoundException, ProyectoNotFoundException {

        Usuario usuario = userService.obtenerUsuarioAutenticado();

        Tarea nuevaTarea = tareaService.crearTarea(request, usuario);

        return ResponseEntity.ok(nuevaTarea);
    }

    @PutMapping("/{idTarea}")
    public ResponseEntity<?> actualizarTarea(@PathVariable Long idTarea, @Valid @RequestBody TareaRequest request) throws UsuarioNotFoundException, TareaNotFoundException, ProyectoNotFoundException {

        Usuario usuario = userService.obtenerUsuarioAutenticado();

        Tarea nuevaTarea = tareaService.actializarTarea(idTarea, request, usuario);

        return ResponseEntity.ok(nuevaTarea);
    }

}
