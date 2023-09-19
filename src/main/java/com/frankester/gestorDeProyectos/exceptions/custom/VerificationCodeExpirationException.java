package com.frankester.gestorDeProyectos.exceptions.custom;

public class VerificationCodeExpirationException extends Throwable{

    public VerificationCodeExpirationException(String message){
        super(message);
    }
}
