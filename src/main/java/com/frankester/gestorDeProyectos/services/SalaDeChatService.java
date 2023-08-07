package com.frankester.gestorDeProyectos.services;

import com.frankester.gestorDeProyectos.exceptions.custom.ChatRoomAlreadyExistException;
import com.frankester.gestorDeProyectos.exceptions.custom.ChatRoomNotFoundException;
import com.frankester.gestorDeProyectos.exceptions.custom.UsuarioNotFoundException;
import com.frankester.gestorDeProyectos.models.DTOs.ChatRoomRequest;
import com.frankester.gestorDeProyectos.models.Proyecto;
import com.frankester.gestorDeProyectos.models.mensajeria.ChatRoom;

public interface SalaDeChatService {

    public ChatRoom crearSalaDeChat(Proyecto proyecto, ChatRoomRequest request) throws ChatRoomAlreadyExistException, UsuarioNotFoundException;

    public ChatRoom actualizarSalaDeChat(Proyecto proyecto, ChatRoomRequest request) throws ChatRoomNotFoundException, UsuarioNotFoundException;

}
