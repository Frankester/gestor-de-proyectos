package com.frankester.gestorDeProyectos.services;


import com.frankester.gestorDeProyectos.exceptions.custom.VerificationCodeExpirationException;
import com.frankester.gestorDeProyectos.exceptions.custom.VerificationCodeInvalidCodeException;
import com.frankester.gestorDeProyectos.exceptions.custom.VerificationCodeTriesExaustedException;
import com.frankester.gestorDeProyectos.models.Usuario;

public interface CodigoDeVerificacionService {
    public String generateVerificationCode();

    public void verifyCode(Usuario usuario, String verificationCode) throws VerificationCodeTriesExaustedException, VerificationCodeInvalidCodeException, VerificationCodeExpirationException;
}
