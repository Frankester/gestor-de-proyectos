package com.frankester.gestorDeProyectos.services.impl;

import com.frankester.gestorDeProyectos.config.jwt.JWTUtils;
import com.frankester.gestorDeProyectos.exceptions.custom.UserAlreadyExistsException;
import com.frankester.gestorDeProyectos.models.DTOs.AuthDTO;
import com.frankester.gestorDeProyectos.models.DTOs.JwtResponse;
import com.frankester.gestorDeProyectos.repositories.RepoUsuarios;
import com.frankester.gestorDeProyectos.services.AuthService;
import com.frankester.gestorDeProyectos.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private RepoUsuarios repo;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private UsuarioService userService;

    @Override
    public String register(AuthDTO registerReq) throws UserAlreadyExistsException {
        if(this.repo.existsByUsername(registerReq.getUsername())){
            throw new UserAlreadyExistsException("Ya existe un usuario con el username: \""+ registerReq.getUsername()+"\"" );
        }

        // devuelve el codigo de verificacion generado
        return this.userService.crearUsuario(registerReq);
    }

    @Override
    public JwtResponse login(AuthDTO login) {

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword())
        );

        String jwt = jwtUtils.createTokenJwt(auth);

        return new JwtResponse(jwt);
    }
}
