package com.frankester.gestorDeProyectos.services;

import com.frankester.gestorDeProyectos.exceptions.custom.VerificationCodeExpirationException;
import com.frankester.gestorDeProyectos.exceptions.custom.VerificationCodeInvalidCodeException;
import com.frankester.gestorDeProyectos.exceptions.custom.VerificationCodeTriesExaustedException;
import com.frankester.gestorDeProyectos.models.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import software.amazon.awssdk.services.s3.S3Client;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CodigoDeVerificacionServiceTest {

    @Autowired
    public CodigoDeVerificacionService codigoDeVerificacionService;

    @MockBean
    private EmailService emailService;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    S3Client s3;

    @Test
    public void se_debe_poder_generar_codigo_verificacion() {

        String code = this.codigoDeVerificacionService.generateVerificationCode();

        assertThat(code.length()).isGreaterThan(20);
    }

    @Test
    public void no_puede_verificar_mas_de_5_intentos() throws VerificationCodeTriesExaustedException, VerificationCodeInvalidCodeException, VerificationCodeExpirationException {
        Usuario usuario = this.mockUsuario();
        usuario.setVerificationCodeTries(5);

        VerificationCodeTriesExaustedException exception = assertThrows(VerificationCodeTriesExaustedException.class, () -> {
            this.codigoDeVerificacionService.verifyCode(usuario, "1234-123131-1231");
        });

        String expectedMessage = "Lo sentimos, se intento verificar el mail mas de 5 veces, por lo tanto la cuenta fue eliminada";
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
        verify(this.usuarioService, times(1)).eliminarUsuario(usuario);
    }

    @Test
    public void el_codigo_debe_ser_igual_al_pasado_via_mail(){
        Usuario usuario = this.mockUsuario();
        usuario.setVerificationCode("1111-111111-1111");


        VerificationCodeInvalidCodeException exception = assertThrows(VerificationCodeInvalidCodeException.class, () -> {
            this.codigoDeVerificacionService.verifyCode(usuario, "1234-123131-1231");
        });

        String expectedMessage = "El codigo de verificacion no coincide con el enviado, intentelo nuevamente, aun le quedan 4 intentos";
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
        verify(this.usuarioService, times(1)).guardarUsuario(usuario);
    }

    @Test
    public void el_codigo_se_debe_verificar_antes_de_que_expire() {
        String fakeVerificationCode = "1234-123131-1231";

        Usuario usuario = this.mockUsuario();
        usuario.setVerificationCodeExpiration(LocalDateTime.of(2023, 1, 10, 10, 0, 0));
        usuario.setVerificationCode(fakeVerificationCode);

        Integer triesBefore = usuario.getVerificationCodeTries() ;

        VerificationCodeExpirationException exception = assertThrows(VerificationCodeExpirationException.class, () -> {
            this.codigoDeVerificacionService.verifyCode(usuario, fakeVerificationCode);
        });

        String expectedMessage = "Lo sentimos, el codigo de verificacion ya expiro, se envio el nuevo codigo de verificacion a su correo, porfabor intentelo nuevamente, le quedan aun 4 intentos";
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
        verify(this.usuarioService, times(1)).guardarUsuario(usuario);
        assertThat(usuario.getVerificationCodeTries()).isEqualTo(triesBefore+1);
    }

    @Test
    public void se_puede_verificar_correctamente_el_codigo_verificacion() throws VerificationCodeTriesExaustedException, VerificationCodeInvalidCodeException, VerificationCodeExpirationException {
        String fakeVerificationCode = "1234-123131-1231";

        Usuario usuario = this.mockUsuario();
        usuario.setVerificationCodeExpiration(LocalDateTime.of(2040, 1, 10, 10, 0, 0));
        usuario.setVerificationCode(fakeVerificationCode);


        this.codigoDeVerificacionService.verifyCode(usuario, fakeVerificationCode);

        assertThat(usuario.getIsEmailVerificated()).isTrue();
        verify(this.usuarioService, times(1)).guardarUsuario(usuario);
    }

    private Usuario mockUsuario(){
        Usuario usuarioMock = new Usuario();
        usuarioMock.setUsername("pepe");
        usuarioMock.setEmail("pepe@gmail.com");

        return usuarioMock;
    }
}
