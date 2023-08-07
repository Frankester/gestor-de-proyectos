package com.frankester.gestorDeProyectos.services;

import com.frankester.gestorDeProyectos.models.Usuario;
import com.frankester.gestorDeProyectos.models.mensajeria.MensajeDeChat;

import java.util.List;

public interface ChatService {

    public void sendMessageToUsers(MensajeDeChat mensajeDeChat, List<Usuario> usuariosAEnviar);
}
