package com.frankester.gestorDeProyectos.services.impl;

import com.frankester.gestorDeProyectos.exceptions.custom.UsuarioNotFoundException;
import com.frankester.gestorDeProyectos.models.DTOs.AuthDTO;
import com.frankester.gestorDeProyectos.models.Usuario;
import com.frankester.gestorDeProyectos.repositories.RepoUsuarios;
import com.frankester.gestorDeProyectos.services.CodigoDeVerificacionService;
import com.frankester.gestorDeProyectos.services.EmailService;
import com.frankester.gestorDeProyectos.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {


    @Autowired
    private RepoUsuarios repoUsuarios;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CodigoDeVerificacionService codigoDeVerificacionService;

    @Autowired
    private EmailService emailService;

    @Override
    public String crearUsuario(AuthDTO request) {
        // Generar el código de verificación (puedes usar UUID o algún otro método)
        String verificationCode = this.codigoDeVerificacionService.generateVerificationCode();

        // Crear un nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(this.passwordEncoder.encode(request.getPassword()));
        usuario.setVerificationCode(verificationCode);
        // 5 horas de validez
        usuario.setVerificationCodeExpiration(LocalDateTime.now().plusHours(5));

        // Guardar el usuario en la base de datos
        this.repoUsuarios.save(usuario);

        // Enviar el código de verificación al correo del usuario
        this.emailService.sendVerificationCode(request.getEmail(), verificationCode);

        return verificationCode;
    }

    @Override
    public Boolean isUserExists(String email){
        return this.repoUsuarios.findByEmail(email) != null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Usuario> usuarioOp = this.repoUsuarios.findByUsername(username);

        if(usuarioOp.isEmpty()){
            throw new UsernameNotFoundException("No se encontro el usuario con el username: '"+ username + "' ");
        }

        Usuario usuario = usuarioOp.get();

        return new User(username, usuario.getPassword(), new ArrayList<>());
    }

    @Override
    public Usuario obtenerUsuarioAutenticado(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication() ;

        org.springframework.security.core.userdetails.User userDetail =
                (org.springframework.security.core.userdetails.User) auth.getPrincipal();


        Optional<Usuario> usuarioOp = this.repoUsuarios.findByUsername(userDetail.getUsername());

        if(usuarioOp.isEmpty()){
            throw new UsernameNotFoundException("No se encontro el usuario con el username: '"+userDetail.getUsername()+"', al intentar de obtenerlo de la peticion");
        }

        return usuarioOp.get();
    }

    @Override
    public Usuario obtenerUsuarioPorUsername(String username) throws UsuarioNotFoundException {
        Optional<Usuario> userOp = this.repoUsuarios.findByUsername(username);

        if(userOp.isEmpty()){
            throw new UsuarioNotFoundException("No existe el usuario con el username: '" +username+ "'");
        }

        return userOp.get();
    }
}
