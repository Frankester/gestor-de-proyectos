package com.frankester.gestorDeProyectos.services.impl;

import com.frankester.gestorDeProyectos.exceptions.custom.ChatRoomAlreadyExistException;
import com.frankester.gestorDeProyectos.exceptions.custom.ChatRoomNotFoundException;
import com.frankester.gestorDeProyectos.exceptions.custom.UsuarioNotFoundException;
import com.frankester.gestorDeProyectos.models.DTOs.ChatRoomRequest;
import com.frankester.gestorDeProyectos.models.Proyecto;
import com.frankester.gestorDeProyectos.models.Usuario;
import com.frankester.gestorDeProyectos.models.mensajeria.ChatRoom;
import com.frankester.gestorDeProyectos.services.ProyectoService;
import com.frankester.gestorDeProyectos.services.SalaDeChatService;
import com.frankester.gestorDeProyectos.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SalaDeChatServiceImpl implements SalaDeChatService {


    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProyectoService proyectoService;

    @Override
    public ChatRoom crearSalaDeChat(Proyecto proyecto, ChatRoomRequest request) throws ChatRoomAlreadyExistException, UsuarioNotFoundException {
        Usuario usuarioAdmin = this.usuarioService.obtenerUsuarioPorUsername(request.getAdminUsername());

        if(proyecto.getSalaDeChat() != null){
            throw new ChatRoomAlreadyExistException("El proyecto "+ proyecto.getNombre()+ " ya posee una sala de chat con el nombre: " + proyecto.getSalaDeChat().getNombre());
        }

        ChatRoom chatRoom = new ChatRoom();

        chatRoom.setNombre(request.getNombre());
        chatRoom.setAdministrador(usuarioAdmin);

        proyecto.setSalaDeChat(chatRoom);

        this.proyectoService.actualizarProyectoModificado(proyecto);

        return chatRoom;
    }

    @Override
    public ChatRoom actualizarSalaDeChat(Proyecto proyecto, ChatRoomRequest request) throws ChatRoomNotFoundException, UsuarioNotFoundException {
        Usuario usuarioAdmin = this.usuarioService.obtenerUsuarioPorUsername(request.getAdminUsername());

        if(proyecto.getSalaDeChat() == null){
            throw new ChatRoomNotFoundException("El proyecto "+ proyecto.getNombre()+ " no posee una sala de chat.");
        }

        ChatRoom chatRoom = proyecto.getSalaDeChat();

        chatRoom.setNombre(request.getNombre());
        chatRoom.setAdministrador(usuarioAdmin);

        proyecto.setSalaDeChat(chatRoom);

        this.proyectoService.actualizarProyectoModificado(proyecto);

        return chatRoom;
    }
}
