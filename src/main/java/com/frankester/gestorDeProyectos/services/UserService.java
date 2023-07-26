package com.frankester.gestorDeProyectos.services;

import com.frankester.gestorDeProyectos.models.DTOs.AuthDTO;
import com.frankester.gestorDeProyectos.models.Usuario;
import com.frankester.gestorDeProyectos.repositories.RepoUsuarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private RepoUsuarios repo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public String registerUser(AuthDTO request) {
        // Generar el código de verificación (puedes usar UUID o algún otro método)
        String verificationCode = generateVerificationCode();

        // Crear un nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setVerificationCode(verificationCode);
        // 5 horas de validez
        usuario.setVerificationCodeExpiration(LocalDateTime.now().plusHours(5));

        // Guardar el usuario en la base de datos
        repo.save(usuario);

        // Enviar el código de verificación al correo del usuario
        emailService.sendVerificationCode(request.getEmail(), verificationCode);

        return verificationCode;
    }


    public String generateVerificationCode() {
        // Genero un rando UUID como codigo de verificacion
        return UUID.randomUUID().toString();
    }

    public Boolean isUserExists(String email){
        return repo.findByEmail(email) != null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Usuario> usuarioOp = this.repo.findByUsername(username);

        if(usuarioOp.isEmpty()){
            throw new UsernameNotFoundException("No se encontro el usuario con el username: '"+ username + "' ");
        }

        Usuario usuario = usuarioOp.get();

        return new User(username, usuario.getPassword(), new ArrayList<>());
    }

    public ResponseEntity<Object> verifyCode(String email, String verificationCode) {
        Usuario usuario = this.repo.findByEmail(email);

        if(usuario.getIsEmailVerificated()){
            return ResponseEntity.badRequest().body("El mail '"+ usuario.getUsername() +"' ya fue verificado con éxito");
        }

        if(usuario.getVerificationCodeTries() >= 5){

            repo.delete(usuario);

            return ResponseEntity.badRequest().body("Lo sentimos, se intento verificar el mail mas de 5 veces, por lo tanto la cuenta fue eliminada");
        }

        if( !verificationCode.equals(usuario.getVerificationCode())){

            usuario.setVerificationCodeTries(usuario.getVerificationCodeTries() + 1);

            repo.save(usuario);

            return ResponseEntity.badRequest().body("El codigo de verificacion no coincide con el enviado, intentelo nuevamente, aun le quedan "+ (usuario.getVerificationCodeTries() +1) +" intentos");
        }

        if(usuario.getVerificationCodeExpiration().isBefore(LocalDateTime.now())){

            String newVerificationCode = generateVerificationCode();
            usuario.setVerificationCode(newVerificationCode);
            usuario.setVerificationCodeExpiration(LocalDateTime.now().plusHours(5));
            usuario.setVerificationCodeTries(usuario.getVerificationCodeTries() + 1);

            //actualizo el usuario
            repo.save(usuario);

            //reenvio el mail
            emailService.sendVerificationCode(usuario.getEmail(), newVerificationCode);


            return ResponseEntity.badRequest().body("Lo sentimos, el codigo de verificacion ya expiro, se envio el nuevo codigo de verificacion a su correo, porfabor intentelo nuevamente, le quedan aun "+ (usuario.getVerificationCodeTries() +1) +" intentos");
        }

        usuario.setIsEmailVerificated(true);

        //actualizo el usuario
        repo.save(usuario);

        return ResponseEntity.ok("Email del usuario '"+ usuario.getUsername() +"' se verifico con éxito");
    }
}
