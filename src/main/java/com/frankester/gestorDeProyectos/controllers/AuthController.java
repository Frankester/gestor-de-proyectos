package com.frankester.gestorDeProyectos.controllers;

import com.frankester.gestorDeProyectos.exceptions.custom.UserAlreadyExistsException;
import com.frankester.gestorDeProyectos.models.DTOs.AuthDTO;
import com.frankester.gestorDeProyectos.models.DTOs.JwtResponse;
import com.frankester.gestorDeProyectos.models.DTOs.VerificationCodeRequest;
import com.frankester.gestorDeProyectos.services.AuthService;
import com.frankester.gestorDeProyectos.services.CodigoDeVerificacionService;
import com.frankester.gestorDeProyectos.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private UsuarioService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private CodigoDeVerificacionService codigoDeVerificacionService;


    @PostMapping("/auth/register")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody AuthDTO request) throws UserAlreadyExistsException {
        // Realizar el registro del usuario y generar el código de verificación, y enviar el mail
        this.authService.register(request);

        return ResponseEntity.ok("Usuario registrado con éxito, verifica tu correo.");
    }

    @PostMapping("/auth/verifycode")
    public ResponseEntity<Object> verifyUserEmail(@Valid @RequestBody VerificationCodeRequest request) {
        // Verificar si el usuario ya existe
        if (!this.userService.isUserExists(request.getEmail())) {
            return ResponseEntity.badRequest().body("El usuario no está registrado.");
        }

        // Realizar la verificacion del codigo de verificación
        return this.codigoDeVerificacionService.verifyCode(request.getEmail(), request.getVerificationCode());
    }

    @PostMapping("/auth/login")
    public ResponseEntity<Object> loginUser(@Valid @RequestBody AuthDTO request) {
        // Verificar si el usuario ya existe
        if (!this.userService.isUserExists(request.getEmail())) {
            return ResponseEntity.badRequest().body("El usuario '"+ request.getUsername() +"' no se encuentra registrado en el sistema.");
        }

        // Generar el JWT para el usuario
        JwtResponse response = this.authService.login(request);

        return ResponseEntity.ok(response);
    }
}
