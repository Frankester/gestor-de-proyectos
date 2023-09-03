package com.frankester.gestorDeProyectos.controllers;

import com.frankester.gestorDeProyectos.exceptions.custom.UserAlreadyExistsException;
import com.frankester.gestorDeProyectos.models.DTOs.AuthDTO;
import com.frankester.gestorDeProyectos.models.DTOs.JwtResponse;
import com.frankester.gestorDeProyectos.models.DTOs.VerificationCodeRequest;
import com.frankester.gestorDeProyectos.services.AuthService;
import com.frankester.gestorDeProyectos.services.CodigoDeVerificacionService;
import com.frankester.gestorDeProyectos.services.ProyectoService;
import com.frankester.gestorDeProyectos.services.UsuarioService;
import com.frankester.gestorDeProyectos.services.impl.ProyectoServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.services.s3.S3Client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AuthControllerTest {

    @Autowired
    private AuthController authController;

    @MockBean
    private UsuarioService userService;

    @MockBean
    private AuthService authService;

    @MockBean
    private CodigoDeVerificacionService codigoDeVerificacionService;


    @MockBean
    S3Client s3;

    @Test
    public void cuando_un_usuario_se_registra_se_debe_llamar_al_servicio_correcto() throws UserAlreadyExistsException {
        AuthDTO request = this.mockAuthRequest();

        when(this.authService.register(request)).thenReturn("123");

        ResponseEntity<Object> responseEntity = this.authController.registerUser(request);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(responseEntity.getBody()).isEqualTo("Usuario registrado con éxito, verifica tu correo.");
    }

    @Test
    public void cuando_un_usuario_se_logea_debe_devolver_el_token_jwt() {
        AuthDTO request = this.mockAuthRequest();

        when(this.userService.isUserExists(request.getEmail())).thenReturn(true);

        JwtResponse jwtResponse = new JwtResponse("myToken");

        when(this.authService.login(request)).thenReturn(jwtResponse);

        ResponseEntity<Object> responseEntity = this.authController.loginUser(request);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(responseEntity.getBody()).isEqualTo(jwtResponse);
    }

    @Test
    public void cuando_un_usuario_se_logea_debe_debe_existir_en_el_sistema() throws UserAlreadyExistsException {
        AuthDTO request = this.mockAuthRequest();

        when(this.userService.isUserExists(request.getEmail())).thenReturn(false);

        ResponseEntity<Object> responseEntity = this.authController.loginUser(request);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(400));
        assertThat(responseEntity.getBody()).isEqualTo("El usuario '"+ request.getUsername() +"' no se encuentra registrado en el sistema.");
    }


    @Test
    public void cuando_un_usuario_verifica_su_mail_se_debe_llamar_al_servicio_correcto() {
        VerificationCodeRequest request = this.mockVerifyCodeRequest();

        when(this.userService.isUserExists(request.getEmail())).thenReturn(true);

        when(this.codigoDeVerificacionService.verifyCode(request.getEmail(), request.getVerificationCode())).thenReturn(ResponseEntity.ok("Email del usuario 'pepe' se verifico con éxito"));

        ResponseEntity<Object> responseEntity = this.authController.verifyUserEmail(request);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(responseEntity.getBody()).isEqualTo("Email del usuario 'pepe' se verifico con éxito");
    }

    @Test
    public void cuando_un_usuario_verifica_su_mail_debe_debe_existir_en_el_sistema() {
        VerificationCodeRequest request = this.mockVerifyCodeRequest();

        when(this.userService.isUserExists(request.getEmail())).thenReturn(false);


        ResponseEntity<Object> responseEntity = this.authController.verifyUserEmail(request);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(400));
        assertThat(responseEntity.getBody()).isEqualTo("El usuario no está registrado en el sistema.");
    }

    private AuthDTO mockAuthRequest(){
        AuthDTO request = new AuthDTO();
        request.setEmail("hola@gmail.com");
        request.setUsername("hola");
        request.setPassword("1234");

        return request;
    }

    private VerificationCodeRequest mockVerifyCodeRequest(){
        VerificationCodeRequest request = new VerificationCodeRequest();
        request.setEmail("hola@gmail.com");
        request.setVerificationCode("1234-code");

        return request;
    }

}
