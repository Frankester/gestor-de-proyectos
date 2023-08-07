package com.frankester.gestorDeProyectos.services.impl;

import com.frankester.gestorDeProyectos.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void sendVerificationCode(String email, String verificationCode){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Código de verificación");
        message.setText("El código de verificación es: " + verificationCode + ".\n\nTienes 5 horas para validarlo.");
        this.javaMailSender.send(message);
    }
}
