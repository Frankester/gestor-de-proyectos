package com.frankester.gestorDeProyectos.services;

import com.frankester.gestorDeProyectos.exceptions.custom.ChatRoomAlreadyExistException;
import com.frankester.gestorDeProyectos.exceptions.custom.ChatRoomNotFoundException;
import com.frankester.gestorDeProyectos.exceptions.custom.ProyectoNotFoundException;
import com.frankester.gestorDeProyectos.exceptions.custom.UsuarioNotFoundException;
import com.frankester.gestorDeProyectos.models.DTOs.ChatRoomRequest;
import com.frankester.gestorDeProyectos.models.DTOs.ProyectoRequest;
import com.frankester.gestorDeProyectos.models.PanelDeControl;
import com.frankester.gestorDeProyectos.models.Proyecto;
import com.frankester.gestorDeProyectos.models.Usuario;
import com.frankester.gestorDeProyectos.models.mensajeria.ChatRoom;
import com.frankester.gestorDeProyectos.repositories.RepoProyectos;
import com.frankester.gestorDeProyectos.repositories.RepoUsuarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProyectoService {

    @Autowired
    private RepoProyectos repoProyectos;

    @Autowired
    private UserService userService;

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
    
    public ChatRoom crearSalaDeChat(Proyecto proyecto, ChatRoomRequest request) throws ChatRoomAlreadyExistException, UsuarioNotFoundException {

        Usuario usuarioAdmin = this.userService.obtnerUsuarioPorUsername(request.getAdminUsername());

        if(proyecto.getSalaDeChat() != null){
            throw new ChatRoomAlreadyExistException("El proyecto "+ proyecto.getNombre()+ " ya posee una sala de chat con el nombre: " + proyecto.getSalaDeChat().getNombre());
        }

        ChatRoom chatRoom = new ChatRoom();

        chatRoom.setNombre(request.getNombre());
        chatRoom.setAdministrador(usuarioAdmin);

        proyecto.setSalaDeChat(chatRoom);

        actualizarProyecto(proyecto);

        return chatRoom;
    }

    public void actualizarProyecto(Proyecto proyecto){
        this.repoProyectos.save(proyecto);
    }

    public ChatRoom actualizarSalaDeChat(Proyecto proyecto, ChatRoomRequest request) throws ChatRoomNotFoundException, UsuarioNotFoundException {

        Usuario usuarioAdmin = userService.obtnerUsuarioPorUsername(request.getAdminUsername());

        if(proyecto.getSalaDeChat() == null){
            throw new ChatRoomNotFoundException("El proyecto "+ proyecto.getNombre()+ " no posee una sala de chat.");
        }

        ChatRoom chatRoom = proyecto.getSalaDeChat();

        chatRoom.setNombre(request.getNombre());
        chatRoom.setAdministrador(usuarioAdmin);

        proyecto.setSalaDeChat(chatRoom);

        actualizarProyecto(proyecto);

        return chatRoom;
    }

    public PanelDeControl crearPanelDeControl(Proyecto proyecto){
        PanelDeControl panelDeControl = new PanelDeControl();

        panelDeControl.setProgresoDelProyecto(proyecto.calcularProgresoDelProyecto());
        panelDeControl.setAlertas(proyecto.obtenerAlertasDelProyecto());
        panelDeControl.setTareasPendientes(proyecto.obtenerTareasPendientes());

        return panelDeControl;
    }

    public PanelDeControl acutalizarPanelDeControl(Proyecto proyecto){

        PanelDeControl panelDeControl = proyecto.getPanelDeControl();

        panelDeControl.setProgresoDelProyecto(proyecto.calcularProgresoDelProyecto());
        panelDeControl.setAlertas(proyecto.obtenerAlertasDelProyecto());
        panelDeControl.setTareasPendientes(proyecto.obtenerTareasPendientes());

        return panelDeControl;
    }

    public Proyecto crearProyecto(ProyectoRequest request) throws ChatRoomAlreadyExistException, UsuarioNotFoundException {
        Proyecto nuevoProyecto = new Proyecto();

        nuevoProyecto.setNombre(request.getNombre());
        nuevoProyecto.setDescripcion(request.getDescripcion());
        nuevoProyecto.setFechaLimite(request.getFechaLimite());

        crearSalaDeChat(nuevoProyecto, request.getSalaDeChat());


        request.getMiembros().forEach((miembro) -> {

            Usuario userMiembro = null;
            try {
                userMiembro = userService.obtnerUsuarioPorUsername(miembro);
            } catch (UsuarioNotFoundException e) {
                throw new RuntimeException(e);
            }

            nuevoProyecto.addMiembro(userMiembro);

        });

        actualizarProyecto(nuevoProyecto);

        return nuevoProyecto;
    }

    public Proyecto actualizarProyecto(Long proyectoId, ProyectoRequest request) throws ProyectoNotFoundException {
        Proyecto proyecto = obtenerProyectoConId(proyectoId);

        proyecto.setNombre(request.getNombre());
        proyecto.setDescripcion(request.getDescripcion());
        proyecto.setFechaLimite(request.getFechaLimite());


        request.getMiembros().forEach((miembro) -> {
            Usuario userMiembro = null;
            try {
                userMiembro = userService.obtnerUsuarioPorUsername(miembro);
            } catch (UsuarioNotFoundException e) {
                throw new RuntimeException(e);
            }

            proyecto.addMiembro(userMiembro);
        });

        actualizarProyecto(proyecto);

        return proyecto;
    }

}
