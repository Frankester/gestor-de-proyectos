package com.frankester.gestorDeProyectos.services;

import com.frankester.gestorDeProyectos.exceptions.custom.ChatRoomAlreadyExistException;
import com.frankester.gestorDeProyectos.exceptions.custom.ProyectoNotFoundException;
import com.frankester.gestorDeProyectos.exceptions.custom.UsuarioNotFoundException;
import com.frankester.gestorDeProyectos.models.DTOs.ProyectoRequest;
import com.frankester.gestorDeProyectos.models.Proyecto;



public interface ProyectoService {

    public Proyecto obtenerProyectoConId(Long proyectoId) throws ProyectoNotFoundException;

    public Proyecto crearProyecto(ProyectoRequest request) throws UsuarioNotFoundException, ChatRoomAlreadyExistException;

    public Proyecto actualizarProyecto(Long proyectoId, ProyectoRequest request) throws ProyectoNotFoundException;

    public void actualizarProyectoModificado(Proyecto proyecto);

}
