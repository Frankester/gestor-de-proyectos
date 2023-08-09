package com.frankester.gestorDeProyectos.services;


import com.frankester.gestorDeProyectos.controllers.ProyectosController;
import com.frankester.gestorDeProyectos.exceptions.custom.ChatRoomAlreadyExistException;
import com.frankester.gestorDeProyectos.exceptions.custom.ChatRoomNotFoundException;
import com.frankester.gestorDeProyectos.exceptions.custom.ProyectoNotFoundException;
import com.frankester.gestorDeProyectos.exceptions.custom.UsuarioNotFoundException;
import com.frankester.gestorDeProyectos.models.DTOs.ChatRoomRequest;
import com.frankester.gestorDeProyectos.models.DTOs.ProyectoRequest;
import com.frankester.gestorDeProyectos.models.Proyecto;
import com.frankester.gestorDeProyectos.models.Usuario;
import com.frankester.gestorDeProyectos.models.mensajeria.ChatRoom;
import com.frankester.gestorDeProyectos.repositories.RepoProyectos;
import com.frankester.gestorDeProyectos.repositories.RepoUsuarios;
import com.frankester.gestorDeProyectos.services.impl.ProyectoServiceImpl;
import com.frankester.gestorDeProyectos.services.impl.SalaDeChatServiceImpl;
import com.frankester.gestorDeProyectos.services.impl.UsuarioServiceImpl;
import jakarta.persistence.EntityManagerFactory;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import software.amazon.awssdk.services.s3.S3Client;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ProyectoServiceTest {

    @TestConfiguration
    static class ProyectoServiceTestContextConfiguration{
        @Bean
        public ProyectoService proyectoService(){
            return new ProyectoServiceImpl();
        }
    }

    @MockBean
    private RepoProyectos repoProyectos;

    @MockBean
    private RepoUsuarios repoUsuarios;

    @Autowired
    private ProyectoService proyectoService;

    @MockBean
    S3Client s3;

    @BeforeEach
    public void setUpTest(){
        Usuario user1 = new Usuario();
        user1.setUsername("user1");

        Usuario user2 = new Usuario();
        user2.setUsername("user2");

        Usuario user3 = new Usuario();
        user3.setUsername("user3");

        when(this.repoUsuarios.findByUsername(user1.getUsername())).thenReturn(Optional.of(user1));
        when(this.repoUsuarios.findByUsername(user2.getUsername())).thenReturn(Optional.of(user2));
        when(this.repoUsuarios.findByUsername(user3.getUsername())).thenReturn(Optional.of(user3));
    }

    @Test
    public void cuando_creo_un_proyecto_debe_devolver_el_proyecto_creado() throws ChatRoomAlreadyExistException, UsuarioNotFoundException {
        ProyectoRequest request = new ProyectoRequest();
        request.setNombre("MegaProyect");
        request.setFechaLimite(LocalDate.of(2040, Month.SEPTEMBER,20 ));
        request.setMiembros(List.of("user1", "user2"));

        ChatRoomRequest chatRoomRequest = new ChatRoomRequest();
        chatRoomRequest.setNombre("megaProjectGroup");
        chatRoomRequest.setAdminUsername("user1");

        request.setSalaDeChat(chatRoomRequest);

        when(this.repoProyectos.save(any(Proyecto.class))).thenReturn(new Proyecto());


        Proyecto proyectoCreado = this.proyectoService.crearProyecto(request);

        assertProyecto(proyectoCreado, request);
    }

    @Test
    public void cuando_actualizo_un_proyecto_debe_devolver_el_proyecto_actualizado() throws UsuarioNotFoundException, ProyectoNotFoundException, ChatRoomNotFoundException {
        ProyectoRequest request = new ProyectoRequest();
        request.setNombre("MegaProyect");
        request.setFechaLimite(LocalDate.of(2040, Month.SEPTEMBER,20 ));
        request.setMiembros(List.of("user1", "user3"));

        ChatRoomRequest chatRoomRequest = new ChatRoomRequest();
        chatRoomRequest.setNombre("megaProjectGroup");
        chatRoomRequest.setAdminUsername("user1");

        request.setSalaDeChat(chatRoomRequest);

        when(this.repoProyectos.save(any(Proyecto.class))).thenReturn(new Proyecto());

        //creo un proyecto mock para testar la actualizacion del mismo
        Proyecto megaProyecto = new Proyecto();
        megaProyecto.setNombre("otherNameProject");

        ChatRoom salaDeChat = new ChatRoom();
        salaDeChat.setNombre("otherChatRoomsName");

        megaProyecto.setSalaDeChat(salaDeChat);

        when(this.repoProyectos.findById(1L)).thenReturn(Optional.of(megaProyecto));


        Proyecto proyectoActualizado = this.proyectoService.actualizarProyecto(1L, request);

        assertProyecto(proyectoActualizado, request);
    }

    @Test
    public void no_se_debe_poder_crear_un_proyecto_con_miembros_sin_registrarse() {
        ProyectoRequest request = new ProyectoRequest();
        request.setNombre("MegaProyect");
        request.setFechaLimite(LocalDate.of(2040, Month.SEPTEMBER,20 ));
        request.setMiembros(List.of("user1", "user2", "user4"));// el user4 no existe

        ChatRoomRequest chatRoomRequest = new ChatRoomRequest();
        chatRoomRequest.setNombre("megaProjectGroup");
        chatRoomRequest.setAdminUsername("user1");

        request.setSalaDeChat(chatRoomRequest);

        when(this.repoProyectos.save(any(Proyecto.class))).thenReturn(new Proyecto());

        UsuarioNotFoundException exception = assertThrows(UsuarioNotFoundException.class, () -> {
            this.proyectoService.crearProyecto(request);
        });

        String mensajeEsperado = "No existe el usuario con el username: 'user4'";
        String mensajeActual = exception.getMessage();

        assertThat(mensajeActual).isEqualTo(mensajeEsperado);

    }


    @Test
    public void no_se_debe_poder_actualizar_un_proyecto_con_miembros_sin_registrarse() {
        ProyectoRequest request = new ProyectoRequest();
        request.setNombre("MegaProyect");
        request.setFechaLimite(LocalDate.of(2040, Month.SEPTEMBER,20 ));
        request.setMiembros(List.of("user1", "user2", "user4"));// el user4 no existe

        ChatRoomRequest chatRoomRequest = new ChatRoomRequest();
        chatRoomRequest.setNombre("megaProjectGroup");
        chatRoomRequest.setAdminUsername("user1");

        request.setSalaDeChat(chatRoomRequest);

        when(this.repoProyectos.save(any(Proyecto.class))).thenReturn(new Proyecto());

        //creo un proyecto mock para testar la actualizacion del mismo
        Proyecto megaProyecto = new Proyecto();
        megaProyecto.setNombre("otherNameProject");

        ChatRoom salaDeChat = new ChatRoom();
        salaDeChat.setNombre("otherChatRoomsName");

        megaProyecto.setSalaDeChat(salaDeChat);

        when(this.repoProyectos.findById(1L)).thenReturn(Optional.of(megaProyecto));

        UsuarioNotFoundException exception = assertThrows(UsuarioNotFoundException.class, () -> {
            this.proyectoService.actualizarProyecto(1L, request);
        });

        String mensajeEsperado = "No existe el usuario con el username: 'user4'";
        String mensajeActual = exception.getMessage();

        assertThat(mensajeActual).isEqualTo(mensajeEsperado);

    }

    @Test
    public void no_se_debe_poder_actualizar_un_proyecto_inexistente() {
        ProyectoRequest request = new ProyectoRequest();
        request.setNombre("MegaProyect");
        request.setFechaLimite(LocalDate.of(2040, Month.SEPTEMBER,20 ));
        request.setMiembros(List.of("user1", "user2", "user4"));// el user4 no existe

        ChatRoomRequest chatRoomRequest = new ChatRoomRequest();
        chatRoomRequest.setNombre("megaProjectGroup");
        chatRoomRequest.setAdminUsername("user1");

        request.setSalaDeChat(chatRoomRequest);

        when(this.repoProyectos.save(any(Proyecto.class))).thenReturn(new Proyecto());

        //no agrego ningun proyecto
        when(this.repoProyectos.findById(1L)).thenReturn(Optional.empty());


        ProyectoNotFoundException exception = assertThrows(ProyectoNotFoundException.class, () -> {
            this.proyectoService.actualizarProyecto(1L, request);
        });

        String mensajeEsperado = "No se encontro el proyecto con id: 1";
        String mensajeActual = exception.getMessage();

        assertThat(mensajeActual).isEqualTo(mensajeEsperado);

    }


    @Test
    public void no_se_debe_poder_actualizar_un_proyecto_no_virgente() {
        ProyectoRequest request = new ProyectoRequest();
        request.setNombre("MegaProyect");
        request.setFechaLimite(LocalDate.of(2040, Month.SEPTEMBER,20 ));
        request.setMiembros(List.of("user1", "user2", "user4"));// el user4 no existe

        ChatRoomRequest chatRoomRequest = new ChatRoomRequest();
        chatRoomRequest.setNombre("megaProjectGroup");
        chatRoomRequest.setAdminUsername("user1");

        request.setSalaDeChat(chatRoomRequest);

        when(this.repoProyectos.save(any(Proyecto.class))).thenReturn(new Proyecto());

        //agrego un Proycto no virgente
        Proyecto proyectoNoVirgente = new Proyecto();
        proyectoNoVirgente.setProyectoVirgente(false);

        when(this.repoProyectos.findById(1L)).thenReturn(Optional.of(proyectoNoVirgente));


        ProyectoNotFoundException exception = assertThrows(ProyectoNotFoundException.class, () -> {
            this.proyectoService.actualizarProyecto(1L, request);
        });

        String mensajeEsperado = "No se encontro el proyecto con id: 1";
        String mensajeActual = exception.getMessage();

        assertThat(mensajeActual).isEqualTo(mensajeEsperado);

    }

    private void assertProyecto(Proyecto proyecto, ProyectoRequest request){
        assertThat(proyecto.getNombre()).isSameAs(request.getNombre());
        assertThat(proyecto.getProyectoVirgente()).isTrue();
        assertThat(proyecto.getMiembros().size()).isSameAs(request.getMiembros().size());

        for(String miembro : request.getMiembros()){
            Usuario userN = new Usuario();
            userN.setUsername(miembro);

            assertTrue(proyecto.getMiembros().contains(userN));
        }

        assertThat(proyecto.getFechaLimite()).isSameAs(request.getFechaLimite());
        assertThat(proyecto.getSalaDeChat().getNombre()).isSameAs(request.getSalaDeChat().getNombre());
    }
}
