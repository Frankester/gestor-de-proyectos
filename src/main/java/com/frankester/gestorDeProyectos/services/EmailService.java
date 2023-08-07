package com.frankester.gestorDeProyectos.services;



public interface EmailService {

    public void sendVerificationCode(String email, String verificationCode);
}
