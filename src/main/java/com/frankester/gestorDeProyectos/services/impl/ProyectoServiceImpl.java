package com.frankester.gestorDeProyectos.services.impl;

import com.frankester.gestorDeProyectos.exceptions.custom.ChatRoomAlreadyExistException;
import com.frankester.gestorDeProyectos.exceptions.custom.ChatRoomNotFoundException;
import com.frankester.gestorDeProyectos.exceptions.custom.ProyectoNotFoundException;
import com.frankester.gestorDeProyectos.exceptions.custom.UsuarioNotFoundException;
import com.frankester.gestorDeProyectos.models.DTOs.ProyectoRequest;
import com.frankester.gestorDeProyectos.models.Proyecto;
import com.frankester.gestorDeProyectos.models.Usuario;
import com.frankester.gestorDeProyectos.repositories.RepoProyectos;
import com.frankester.gestorDeProyectos.services.ProyectoService;
import com.frankester.gestorDeProyectos.services.SalaDeChatService;
import com.frankester.gestorDeProyectos.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProyectoServiceImpl implements ProyectoService {

    @Autowired
    private RepoProyectos repoProyectos;

    @Autowired
    private UsuarioService userService;

    @Autowired
    private SalaDeChatService salaDeChatService;


    @Override
    public Proyecto obtenerProyectoConId(Long proyectoId) throws ProyectoNotFoundException {
        Optional<Proyecto> proyectoOp = this.repoProyectos.findById(proyectoId);

        if(proyectoOp.isEmpty()){
            throw new ProyectoNotFoundException("No se encontro el proyecto con id: " + proyectoId);

        }
        Proyecto proyecto = proyectoOp.get();

        if(!proyecto.getProyectoVirgente()){
            throw new ProyectoNotFoundException("No se encontro el proyecto con id: " + proyectoId);
        }

        return proyecto;
    }

    @Override
    public Proyecto crearProyecto(ProyectoRequest request) throws UsuarioNotFoundException, ChatRoomAlreadyExistException {
        Proyecto nuevoProyecto = new Proyecto();

        nuevoProyecto.setNombre(request.getNombre());
        nuevoProyecto.setDescripcion(request.getDescripcion());
        nuevoProyecto.setFechaLimite(request.getFechaLimite());

        this.salaDeChatService.crearSalaDeChat(nuevoProyecto, request.getSalaDeChat());

        for( String miembro:request.getMiembros()){
            Usuario userMiembro = this.userService.obtenerUsuarioPorUsername(miembro);

            nuevoProyecto.addMiembro(userMiembro);
        }

        actualizarProyectoModificado(nuevoProyecto);

        return nuevoProyecto;
    }

    @Override
    public Proyecto actualizarProyecto(Long proyectoId, ProyectoRequest request) throws ProyectoNotFoundException, UsuarioNotFoundException, ChatRoomNotFoundException {
        Proyecto proyecto = obtenerProyectoConId(proyectoId);

        proyecto.setNombre(request.getNombre());
        proyecto.setDescripcion(request.getDescripcion());
        proyecto.setFechaLimite(request.getFechaLimite());


        for(String miembro: request.getMiembros()){
            Usuario userMiembro = this.userService.obtenerUsuarioPorUsername(miembro);
            proyecto.addMiembro(userMiembro);
        }

        this.salaDeChatService.actualizarSalaDeChat(proyecto, request.getSalaDeChat());

        actualizarProyectoModificado(proyecto);

        return proyecto;
    }

    @Override
    public void actualizarProyectoModificado(Proyecto proyecto){
        this.repoProyectos.save(proyecto);
    }
}
