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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
public class ChatController {

    @Autowired
    public ChatService chatService;

    @Autowired
    public RepoProyectos repoProyectos;

    @MessageMapping("/chat/{projectId}")
    public void sendMessage(@DestinationVariable Long projectId, MensajeDeChat message) throws ProyectoNotFoundException, UsuarioNotFoundException {

        Optional<Proyecto> proyectoOp = this.repoProyectos.findById(projectId);

        if(proyectoOp.isEmpty()){
            throw new ProyectoNotFoundException("No se encontro un proyecto con id: "+ projectId);
        }

        Proyecto proyecto = proyectoOp.get();

        Optional<Usuario> userOp = proyecto
                .getMiembros().stream()
                .filter(mimebro -> mimebro.getUsername().equals(message.getUsername()))
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

        repoProyectos.save(proyecto);

        chatService.sendMessageToUsers(message, proyecto.getMiembros());
    }
}
