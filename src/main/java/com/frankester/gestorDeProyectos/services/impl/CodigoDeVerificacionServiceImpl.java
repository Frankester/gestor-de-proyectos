package com.frankester.gestorDeProyectos.services.impl;

import com.frankester.gestorDeProyectos.models.Usuario;
import com.frankester.gestorDeProyectos.repositories.RepoUsuarios;
import com.frankester.gestorDeProyectos.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CodigoDeVerificacionServiceImpl implements com.frankester.gestorDeProyectos.services.CodigoDeVerificacionService {


    @Autowired
    private EmailService emailService;

    @Autowired
    private RepoUsuarios repoUsuarios;

    @Override
    public String generateVerificationCode() {
        // Genero un rando UUID como codigo de verificacion
        return UUID.randomUUID().toString();
    }

    @Override
    public ResponseEntity<Object> verifyCode(String email, String verificationCode) {
        Usuario usuario = this.repoUsuarios.findByEmail(email);

        if(usuario.getIsEmailVerificated()){
            return ResponseEntity.badRequest().body("El mail '"+ usuario.getUsername() +"' ya fue verificado con éxito");
        }

        if(usuario.getVerificationCodeTries() >= 5){

            this.repoUsuarios.delete(usuario);

            return ResponseEntity.badRequest().body("Lo sentimos, se intento verificar el mail mas de 5 veces, por lo tanto la cuenta fue eliminada");
        }

        if( !verificationCode.equals(usuario.getVerificationCode())){

            usuario.setVerificationCodeTries(usuario.getVerificationCodeTries() + 1);

            this.repoUsuarios.save(usuario);

            return ResponseEntity.badRequest().body("El codigo de verificacion no coincide con el enviado, intentelo nuevamente, aun le quedan "+ (usuario.getVerificationCodeTries() +1) +" intentos");
        }

        if(usuario.getVerificationCodeExpiration().isBefore(LocalDateTime.now())){

            String newVerificationCode = generateVerificationCode();
            usuario.setVerificationCode(newVerificationCode);
            usuario.setVerificationCodeExpiration(LocalDateTime.now().plusHours(5));
            usuario.setVerificationCodeTries(usuario.getVerificationCodeTries() + 1);

            //actualizo el usuario
            this.repoUsuarios.save(usuario);

            //reenvio el mail
            this.emailService.sendVerificationCode(usuario.getEmail(), newVerificationCode);


            return ResponseEntity.badRequest().body("Lo sentimos, el codigo de verificacion ya expiro, se envio el nuevo codigo de verificacion a su correo, porfabor intentelo nuevamente, le quedan aun "+ (usuario.getVerificationCodeTries() +1) +" intentos");
        }

        usuario.setIsEmailVerificated(true);

        //actualizo el usuario
        this.repoUsuarios.save(usuario);

        return ResponseEntity.ok("Email del usuario '"+ usuario.getUsername() +"' se verifico con éxito");
    }
}
