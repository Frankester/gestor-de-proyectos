package com.frankester.gestorDeProyectos.services.impl;

import com.frankester.gestorDeProyectos.models.Usuario;
import com.frankester.gestorDeProyectos.models.mensajeria.MensajeDeChat;
import com.frankester.gestorDeProyectos.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
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
