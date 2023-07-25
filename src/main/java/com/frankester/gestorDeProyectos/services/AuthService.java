package com.frankester.gestorDeProyectos.services;

import com.frankester.gestorDeProyectos.config.jwt.JWTUtils;
import com.frankester.gestorDeProyectos.exceptions.custom.UserAlreadyExistsException;
import com.frankester.gestorDeProyectos.models.DTOs.AuthDTO;
import com.frankester.gestorDeProyectos.models.DTOs.JwtResponse;
import com.frankester.gestorDeProyectos.models.Usuario;
import com.frankester.gestorDeProyectos.repositories.RepoUsuarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private RepoUsuarios repo;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void register(AuthDTO registerReq) throws UserAlreadyExistsException {
        if(this.repo.existsByUsername(registerReq.getUsername())){
            throw new UserAlreadyExistsException("Ya existe un usuario con el username: \""+ registerReq.getUsername()+"\"" );
        }


        Usuario user = new Usuario();

        user.setUsername(registerReq.getUsername());
        user.setPassword(passwordEncoder.encode(registerReq.getPassword()));

        this.repo.save(user);

    }

    public JwtResponse login(AuthDTO login) {

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword())
        );

        String jwt = jwtUtils.createTokenJwt(auth);

        return new JwtResponse(jwt);
    }
}
