package com.frankester.gestorDeProyectos.controllers;

import com.frankester.gestorDeProyectos.exceptions.custom.*;
import com.frankester.gestorDeProyectos.models.DTOs.ChatRoomRequest;
import com.frankester.gestorDeProyectos.models.DTOs.ProyectoRequest;
import com.frankester.gestorDeProyectos.models.DTOs.TareaRequest;
import com.frankester.gestorDeProyectos.models.PanelDeControl;
import com.frankester.gestorDeProyectos.models.Proyecto;
import com.frankester.gestorDeProyectos.models.Tarea;
import com.frankester.gestorDeProyectos.models.Usuario;
import com.frankester.gestorDeProyectos.models.mensajeria.ChatRoom;
import com.frankester.gestorDeProyectos.services.PanelDeControlService;
import com.frankester.gestorDeProyectos.services.ProyectoService;
import com.frankester.gestorDeProyectos.services.SalaDeChatService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RepositoryRestController(path = "proyectos")
public class ProyectosController {

    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private SalaDeChatService salaDeChatService;

    @Autowired
    private PanelDeControlService panelDeControlService;

    @DeleteMapping("/{idProyecto}")
    public ResponseEntity<?> borrarProyecto(@PathVariable Long idProyecto) throws ProyectoNotFoundException {

        Proyecto proyecto = this.proyectoService.obtenerProyectoConId(idProyecto);

        proyecto.setProyectoVirgente(false);

        this.proyectoService.actualizarProyectoModificado(proyecto);

        return ResponseEntity.ok("Se elimino el proyecto " + proyecto.getNombre() + " correctamente.");
    }

    @PostMapping("/{idProyecto}/salaDeChat")
    public ResponseEntity<?> crearSalaDeChat(@PathVariable Long idProyecto, @Valid @RequestBody ChatRoomRequest request) throws ProyectoNotFoundException, ChatRoomAlreadyExistException, UsuarioNotFoundException {


        Proyecto proyecto = this.proyectoService.obtenerProyectoConId(idProyecto);

        ChatRoom salaDeChat = this.salaDeChatService.crearSalaDeChat(proyecto, request);

        return ResponseEntity.ok(salaDeChat);
    }

    @PutMapping("/{idProyecto}/salaDeChat")
    public ResponseEntity<?> actualizarSalaDeChat(@PathVariable Long idProyecto, @Valid @RequestBody ChatRoomRequest request) throws ProyectoNotFoundException, ChatRoomNotFoundException, UsuarioNotFoundException {

        Proyecto proyecto = this.proyectoService.obtenerProyectoConId(idProyecto);

        ChatRoom salaDeChat = this.salaDeChatService.actualizarSalaDeChat(proyecto, request);

        return ResponseEntity.ok(salaDeChat);
    }

    @GetMapping("/{idProyecto}/panelDeControl")
    public ResponseEntity<?> obtenerPanelDeControl(@PathVariable Long idProyecto) throws ProyectoNotFoundException {

        Proyecto proyecto = this.proyectoService.obtenerProyectoConId(idProyecto);

        PanelDeControl panelDeControl;

        if(proyecto.getPanelDeControl() == null){
            panelDeControl = this.panelDeControlService.crearPanelDeControl(proyecto);
        } else {
            panelDeControl = this.panelDeControlService.acutalizarPanelDeControl(proyecto);
        }

        this.proyectoService.actualizarProyectoModificado(proyecto);

        return ResponseEntity.ok(panelDeControl);
    }

    @PostMapping(path = {"/", ""})
    public ResponseEntity<?> crearProyecto(@Valid @RequestBody ProyectoRequest request) throws ChatRoomAlreadyExistException, UsuarioNotFoundException {

        Proyecto nuevoProyecto = this.proyectoService.crearProyecto(request);

        return ResponseEntity.ok(nuevoProyecto);
    }

    @PutMapping("/{idTarea}")
    public ResponseEntity<?> actualizarProyecto(@PathVariable Long idProyecto, @Valid @RequestBody ProyectoRequest request) throws ProyectoNotFoundException {

        Proyecto nuevoProyecto = this.proyectoService.actualizarProyecto(idProyecto, request);

        return ResponseEntity.ok(nuevoProyecto);
    }

}
