package com.frankester.gestorDeProyectos.controllers;

import com.frankester.gestorDeProyectos.exceptions.custom.ProyectoNotFoundException;
import com.frankester.gestorDeProyectos.exceptions.custom.UsuarioNotFoundException;
import com.frankester.gestorDeProyectos.models.Proyecto;
import com.frankester.gestorDeProyectos.models.Usuario;
import com.frankester.gestorDeProyectos.models.mensajeria.ChatRoom;
import com.frankester.gestorDeProyectos.models.mensajeria.Mensaje;
import com.frankester.gestorDeProyectos.models.mensajeria.MensajeDeChat;
import com.frankester.gestorDeProyectos.repositories.RepoProyectos;
import com.frankester.gestorDeProyectos.services.ChatService;
import com.frankester.gestorDeProyectos.services.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
public class ChatController {


    private ChatService chatService;


    private ProyectoService proyectoService;


    @Autowired
    public ChatController(ChatService chatService, ProyectoService proyectoService) {
        this.chatService = chatService;
        this.proyectoService = proyectoService;
    }

    @MessageMapping("/chat/{projectId}")
    public void sendMessage(@DestinationVariable Long projectId, MensajeDeChat message) throws ProyectoNotFoundException, UsuarioNotFoundException {

        Proyecto proyecto = this.proyectoService.obtenerProyectoConId(projectId);

        Optional<Usuario> userOp = proyecto
                .getMiembros().stream()
                .filter(miembro -> miembro.getUsername().equals(message.getUsername()))
                .findFirst();

        if(userOp.isEmpty()){
            throw new UsuarioNotFoundException("No se encontro al miembro con el username: " + message.getUsername());
        }

        Usuario usuario = userOp.get();

        Mensaje mensajeAGuardar = new Mensaje();
        mensajeAGuardar.setMensaje(message.getMensaje());
        mensajeAGuardar.setUsuario(usuario);

        ChatRoom chatRoom = proyecto.getSalaDeChat();
        chatRoom.addMensaje(mensajeAGuardar);

        this.proyectoService.actualizarProyectoModificado(proyecto);

        this.chatService.sendMessageToUsers(message, proyecto.getMiembros());
    }
}
