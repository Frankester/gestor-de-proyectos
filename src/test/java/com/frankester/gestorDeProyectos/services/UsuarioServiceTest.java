package com.frankester.gestorDeProyectos.services;

import com.frankester.gestorDeProyectos.exceptions.custom.UserAlreadyExistsException;
import com.frankester.gestorDeProyectos.models.DTOs.AuthDTO;
import com.frankester.gestorDeProyectos.models.Usuario;
import com.frankester.gestorDeProyectos.repositories.RepoUsuarios;
import com.frankester.gestorDeProyectos.services.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UsuarioServiceTest {

    @Autowired
    public UsuarioService usuarioService;

    @MockBean
    public RepoUsuarios repoUsuarios;

    @MockBean
    public EmailService emailService;

    @MockBean
    S3Client s3;


    @Test
    public void cuando_creo_un_usuario_debe_devolver_el_codigo_de_verificacion() throws UserAlreadyExistsException {
        AuthDTO request = new AuthDTO();
        request.setUsername("user1");
        request.setPassword("123");
        request.setEmail("fake@gmail.com");


        when(this.repoUsuarios.save(any(Usuario.class))).thenReturn(new Usuario());

        String verifyCode = this.usuarioService.crearUsuario(request);

        verify(this.emailService, times(1)).sendVerificationCode(anyString(), anyString());

        assertThat(verifyCode.length()).isGreaterThan(1);

    }

    @Test
    public void puedo_obtener_un_usuario_por_username(){
        Usuario usuario = new Usuario();
        usuario.setUsername("pepe");
        usuario.setPassword("1234");
        when(this.repoUsuarios.findByUsername(anyString())).thenReturn(Optional.of(usuario));

        UserDetails user = this.usuarioService.loadUserByUsername("pepe");

        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo(usuario.getUsername());
        assertThat(user.getPassword()).isEqualTo(usuario.getPassword());
        assertThat(user.getAuthorities().size()).isEqualTo(0);

    }

}
