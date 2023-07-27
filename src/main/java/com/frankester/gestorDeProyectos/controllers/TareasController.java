package com.frankester.gestorDeProyectos.controllers;

import com.frankester.gestorDeProyectos.exceptions.custom.TareaNotFoundException;
import com.frankester.gestorDeProyectos.exceptions.custom.UsuarioNotFoundException;
import com.frankester.gestorDeProyectos.models.DTOs.TareaRequest;
import com.frankester.gestorDeProyectos.models.Proyecto;
import com.frankester.gestorDeProyectos.models.Tarea;
import com.frankester.gestorDeProyectos.models.Usuario;
import com.frankester.gestorDeProyectos.services.TareaService;
import com.frankester.gestorDeProyectos.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RepositoryRestController(path = "/tareas")
public class TareasController {


    @Autowired
    public TareaService tareaService;

    @Autowired
    public UserService userService;

    @DeleteMapping("/{idTarea}")
    public ResponseEntity<?> borrarTarea(@PathVariable Long idTarea) throws TareaNotFoundException {
        Tarea tarea = this.tareaService.obtenerTareaConId(idTarea);

        tarea.setTareaVirgente(false);

        this.tareaService.actualizarTarea(tarea);

        return ResponseEntity.ok("Se elimino la tarea " + tarea.getTitulo() + " correctamente. para el proyecto '" +tarea.getProyecto().getNombre()+ "'");

    }

    @PostMapping("/{idTarea}/archivos")
    public ResponseEntity<?> guardarArchivoNuevo(@PathVariable Long idTarea, @RequestParam("file") Map<String, MultipartFile> requestFiles) throws TareaNotFoundException {

        Tarea tarea = this.tareaService.obtenerTareaConId(idTarea);

        this.tareaService.guardarArchivos(requestFiles, tarea);


        return ResponseEntity.ok("Se agregaron " +requestFiles.size()+ " archivos para la tarea '" +tarea.getTitulo()+ "' con exito");
    }

    @PostMapping(path = {"/", ""})
    public ResponseEntity<?> crearTarea(@RequestBody TareaRequest request) throws UsuarioNotFoundException {

        Usuario usuario = userService.obtnerUsuarioAutenticado();

        Tarea nuevaTarea = tareaService.crearTarea(request, usuario);

        return ResponseEntity.ok(nuevaTarea);
    }

    @PutMapping("/{idTarea}")
    public ResponseEntity<?> crearTarea(@PathVariable Long idTarea, @RequestBody TareaRequest request) throws UsuarioNotFoundException, TareaNotFoundException {

        Usuario usuario = userService.obtnerUsuarioAutenticado();

        Tarea nuevaTarea = tareaService.actializarTarea(idTarea, request, usuario);

        return ResponseEntity.ok(nuevaTarea);
    }

}
