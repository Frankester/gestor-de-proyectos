package com.frankester.gestorDeProyectos.services;

import com.frankester.gestorDeProyectos.exceptions.custom.UserAlreadyExistsException;
import com.frankester.gestorDeProyectos.models.DTOs.AuthDTO;
import com.frankester.gestorDeProyectos.models.DTOs.JwtResponse;


public interface AuthService {

    public String register(AuthDTO registerReq) throws UserAlreadyExistsException;

    public JwtResponse login(AuthDTO login);
}
