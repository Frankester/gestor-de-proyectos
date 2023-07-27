package com.frankester.gestorDeProyectos.services;

import com.frankester.gestorDeProyectos.models.Usuario;
import com.frankester.gestorDeProyectos.models.mensajeria.MensajeDeChat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {


    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendMessageToUsers(MensajeDeChat mensajeDeChat, List<Usuario> usuariosAEnviar){

        usuariosAEnviar.forEach(user -> {
            messagingTemplate.convertAndSendToUser(
                    user.getUsername(),
                    "/queue/chat",
                    mensajeDeChat
            );
        });
    }

}
