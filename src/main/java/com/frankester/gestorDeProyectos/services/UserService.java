package com.frankester.gestorDeProyectos.services;

import com.frankester.gestorDeProyectos.models.Usuario;
import com.frankester.gestorDeProyectos.repositories.RepoUsuarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private RepoUsuarios repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Usuario> usuarioOp = this.repo.findByUsername(username);

        if(usuarioOp.isEmpty()){
            throw new UsernameNotFoundException("No se encontro el usuario con el username: '"+ username + "' ");
        }

        Usuario usuario = usuarioOp.get();

        return new User(username, usuario.getPassword(), new ArrayList<>());
    }
}
