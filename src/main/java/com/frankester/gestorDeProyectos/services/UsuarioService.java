package com.frankester.gestorDeProyectos.services;

import com.frankester.gestorDeProyectos.exceptions.custom.UsuarioNotFoundException;
import com.frankester.gestorDeProyectos.models.DTOs.AuthDTO;
import com.frankester.gestorDeProyectos.models.Usuario;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface UsuarioService extends UserDetailsService {

    public String crearUsuario(AuthDTO request);

    public Boolean isUserExists(String email);

    public Usuario obtenerUsuarioAutenticado();

    public Usuario obtenerUsuarioPorUsername(String username) throws UsuarioNotFoundException ;

}
