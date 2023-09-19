package com.frankester.gestorDeProyectos.services.impl;

import com.frankester.gestorDeProyectos.exceptions.custom.UsuarioNotFoundException;
import com.frankester.gestorDeProyectos.exceptions.custom.VerificationCodeExpirationException;
import com.frankester.gestorDeProyectos.exceptions.custom.VerificationCodeInvalidCodeException;
import com.frankester.gestorDeProyectos.exceptions.custom.VerificationCodeTriesExaustedException;
import com.frankester.gestorDeProyectos.models.Usuario;
import com.frankester.gestorDeProyectos.repositories.RepoUsuarios;
import com.frankester.gestorDeProyectos.services.EmailService;
import com.frankester.gestorDeProyectos.services.UsuarioService;
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
    private UsuarioService usuarioService;


    private Integer intentosMaximos = 5;

    @Override
    public String generateVerificationCode() {
        // Genero un rando UUID como codigo de verificacion
        return UUID.randomUUID().toString();
    }

    @Override
    public void verifyCode(Usuario usuario, String verificationCode) throws VerificationCodeTriesExaustedException, VerificationCodeInvalidCodeException, VerificationCodeExpirationException {

        if(usuario.getVerificationCodeTries() >= intentosMaximos){

            this.usuarioService.eliminarUsuario(usuario);

            //bad request
            throw new VerificationCodeTriesExaustedException("Lo sentimos, se intento verificar el mail mas de 5 veces, por lo tanto la cuenta fue eliminada");
        }

        if( !verificationCode.equals(usuario.getVerificationCode())){

            usuario.setVerificationCodeTries(usuario.getVerificationCodeTries() + 1);

            this.usuarioService.guardarUsuario(usuario);

            throw new VerificationCodeInvalidCodeException("El codigo de verificacion no coincide con el enviado, intentelo nuevamente, aun le quedan "+ (intentosMaximos-usuario.getVerificationCodeTries()) +" intentos");
        }

        if(usuario.getVerificationCodeExpiration().isBefore(LocalDateTime.now())){

            String newVerificationCode = generateVerificationCode();
            usuario.setVerificationCode(newVerificationCode);
            usuario.setVerificationCodeExpiration(LocalDateTime.now().plusHours(5));
            usuario.setVerificationCodeTries(usuario.getVerificationCodeTries() + 1);

            this.usuarioService.guardarUsuario(usuario);

            //reenvio el mail
            this.emailService.sendVerificationCode(usuario.getEmail(), newVerificationCode);


            throw new VerificationCodeExpirationException("Lo sentimos, el codigo de verificacion ya expiro, se envio el nuevo codigo de verificacion a su correo, porfabor intentelo nuevamente, le quedan aun "+ (intentosMaximos-usuario.getVerificationCodeTries()) +" intentos");
        }

        usuario.setIsEmailVerificated(true);

        this.usuarioService.guardarUsuario(usuario);

    }
}
