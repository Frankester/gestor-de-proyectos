package com.frankester.gestorDeProyectos.services;

import org.springframework.http.ResponseEntity;

public interface CodigoDeVerificacionService {
    public String generateVerificationCode();

    public ResponseEntity<Object> verifyCode(String email, String verificationCode);
}
