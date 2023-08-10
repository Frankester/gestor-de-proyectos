package com.frankester.gestorDeProyectos.services;

import com.frankester.gestorDeProyectos.exceptions.custom.ChatRoomAlreadyExistException;
import com.frankester.gestorDeProyectos.exceptions.custom.ProyectoNotFoundException;
import com.frankester.gestorDeProyectos.exceptions.custom.TareaNotFoundException;
import com.frankester.gestorDeProyectos.exceptions.custom.UsuarioNotFoundException;
import com.frankester.gestorDeProyectos.models.DTOs.ChatRoomRequest;
import com.frankester.gestorDeProyectos.models.DTOs.ProyectoRequest;
import com.frankester.gestorDeProyectos.models.DTOs.TareaRequest;
import com.frankester.gestorDeProyectos.models.Proyecto;
import com.frankester.gestorDeProyectos.models.Tarea;
import com.frankester.gestorDeProyectos.models.Usuario;
import com.frankester.gestorDeProyectos.models.estados.EstadoTarea;
import com.frankester.gestorDeProyectos.models.estados.Prioridad;
import com.frankester.gestorDeProyectos.models.mensajeria.Mensaje;
import com.frankester.gestorDeProyectos.repositories.RepoProyectos;
import com.frankester.gestorDeProyectos.repositories.RepoTareas;
import com.frankester.gestorDeProyectos.repositories.RepoUsuarios;
import com.frankester.gestorDeProyectos.services.impl.ProyectoServiceImpl;
import com.frankester.gestorDeProyectos.services.impl.TareaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import software.amazon.awssdk.services.s3.S3Client;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TareasServiceTest {

    @TestConfiguration
    static class TareasServiceTestContextConfiguration{
        @Bean
        public TareaService tareaService(){
            return new TareaServiceImpl();
        }
    }

    @MockBean
    private RepoUsuarios repoUsuarios;

    @MockBean
    private RepoProyectos repoProyectos;

    @MockBean
    private RepoTareas repoTareas;

    @Autowired
    private TareaService tareaService;

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
    public void cuando_creo_una_tarea_debe_devolver_la_tarea_creada() throws UsuarioNotFoundException, ProyectoNotFoundException {
        TareaRequest request = new TareaRequest();
        request.setTitulo("implementar x cosa");
        request.setFechaLimite(LocalDate.of(2040, Month.SEPTEMBER,20 ));
        request.setEstado(EstadoTarea.PENDIENTE);
        request.setUsername("user2");
        request.setPrioridad(Prioridad.ALTA);
        request.setIdProyecto(1L);

        when(this.repoProyectos.findById(1L)).thenReturn(Optional.of(new Proyecto()));
        when(this.repoProyectos.save(any(Proyecto.class))).thenReturn(new Proyecto());
        when(this.repoTareas.save(any(Tarea.class))).thenReturn(new Tarea());

        Usuario usuarioCreadorDeLaTarea = new Usuario();
        usuarioCreadorDeLaTarea.setUsername("user1");

        Tarea tareaCreada = this.tareaService.crearTarea(request,usuarioCreadorDeLaTarea);

        assertTarea(tareaCreada, request, usuarioCreadorDeLaTarea);
    }

    @Test
    public void se_puede_agregar_comentarios_al_crear_la_tarea() throws UsuarioNotFoundException, ProyectoNotFoundException {
        TareaRequest request = new TareaRequest();
        request.setTitulo("implementar x cosa");
        request.setFechaLimite(LocalDate.of(2040, Month.SEPTEMBER,20 ));
        request.setEstado(EstadoTarea.PENDIENTE);
        request.setUsername("user2");
        request.setPrioridad(Prioridad.ALTA);
        request.setIdProyecto(1L);
        request.setComentarios(List.of("un comentario cualquiera", "y otro"));

        when(this.repoProyectos.findById(1L)).thenReturn(Optional.of(new Proyecto()));
        when(this.repoProyectos.save(any(Proyecto.class))).thenReturn(new Proyecto());
        when(this.repoTareas.save(any(Tarea.class))).thenReturn(new Tarea());

        Usuario usuarioCreadorDeLaTarea = new Usuario();
        usuarioCreadorDeLaTarea.setUsername("user1");

        Tarea tareaCreada = this.tareaService.crearTarea(request,usuarioCreadorDeLaTarea);

        assertTarea(tareaCreada, request, usuarioCreadorDeLaTarea);
    }


    @Test
    public void cuando_actualizo_una_tarea_debe_devolver_la_tarea_actualizada() throws UsuarioNotFoundException, ProyectoNotFoundException, TareaNotFoundException {
        TareaRequest request = new TareaRequest();
        request.setTitulo("implementar x cosa");
        request.setFechaLimite(LocalDate.of(2040, Month.SEPTEMBER,20 ));
        request.setEstado(EstadoTarea.PENDIENTE);
        request.setUsername("user2");
        request.setPrioridad(Prioridad.ALTA);
        request.setIdProyecto(1L);

        when(this.repoProyectos.findById(1L)).thenReturn(Optional.of(new Proyecto()));
        when(this.repoProyectos.save(any(Proyecto.class))).thenReturn(new Proyecto());

        when(this.repoTareas.findById(1L)).thenReturn(Optional.of(new Tarea()));
        when(this.repoTareas.save(any(Tarea.class))).thenReturn(new Tarea());

        Usuario usuarioCreadorDeLaTarea = new Usuario();
        usuarioCreadorDeLaTarea.setUsername("user1");

        Tarea tareaActualizada = this.tareaService.actializarTarea(1L,request,usuarioCreadorDeLaTarea);

        assertTarea(tareaActualizada, request, usuarioCreadorDeLaTarea);
    }

    @Test
    public void se_puede_agregar_comentarios_al_actualizar_la_tarea() throws UsuarioNotFoundException, ProyectoNotFoundException, TareaNotFoundException {
        TareaRequest request = new TareaRequest();
        request.setTitulo("implementar x cosa");
        request.setFechaLimite(LocalDate.of(2040, Month.SEPTEMBER,20 ));
        request.setEstado(EstadoTarea.PENDIENTE);
        request.setUsername("user2");
        request.setPrioridad(Prioridad.ALTA);
        request.setIdProyecto(1L);
        request.setComentarios(List.of("comentario 1", "comentario 2", "comentario 3"));

        when(this.repoProyectos.findById(1L)).thenReturn(Optional.of(new Proyecto()));
        when(this.repoProyectos.save(any(Proyecto.class))).thenReturn(new Proyecto());

        when(this.repoTareas.findById(1L)).thenReturn(Optional.of(new Tarea()));
        when(this.repoTareas.save(any(Tarea.class))).thenReturn(new Tarea());

        Usuario usuarioCreadorDeLaTarea = new Usuario();
        usuarioCreadorDeLaTarea.setUsername("user1");

        Tarea tareaActualizada = this.tareaService.actializarTarea(1L,request,usuarioCreadorDeLaTarea);

        assertTarea(tareaActualizada, request, usuarioCreadorDeLaTarea);
    }

    @Test
    public void no_se_puede_asignar_una_tarea_a_un_usuario_no_registrado() {
        TareaRequest request = new TareaRequest();
        request.setTitulo("implementar x cosa");
        request.setFechaLimite(LocalDate.of(2040, Month.SEPTEMBER,20 ));
        request.setEstado(EstadoTarea.PENDIENTE);
        request.setUsername("user6");//no existe el user6
        request.setPrioridad(Prioridad.ALTA);
        request.setIdProyecto(1L);

        when(this.repoProyectos.findById(1L)).thenReturn(Optional.of(new Proyecto()));
        when(this.repoProyectos.save(any(Proyecto.class))).thenReturn(new Proyecto());
        when(this.repoTareas.save(any(Tarea.class))).thenReturn(new Tarea());

        Usuario usuarioCreadorDeLaTarea = new Usuario();
        usuarioCreadorDeLaTarea.setUsername("user1");

        UsuarioNotFoundException exception = assertThrows(UsuarioNotFoundException.class, () ->{
            this.tareaService.crearTarea(request,usuarioCreadorDeLaTarea);
        });

        String mensajeEsperado = "No existe el usuario con el username: 'user6'";
        String mensajeActual = exception.getMessage();

        assertThat(mensajeActual).isEqualTo(mensajeEsperado);
    }

    @Test
    public void no_se_puede_crear_una_tarea_para_un_proyecto_inexistente() {
        TareaRequest request = new TareaRequest();
        request.setTitulo("implementar x cosa");
        request.setFechaLimite(LocalDate.of(2040, Month.SEPTEMBER,20 ));
        request.setEstado(EstadoTarea.PENDIENTE);
        request.setUsername("user3");
        request.setPrioridad(Prioridad.ALTA);
        request.setIdProyecto(1L);

        when(this.repoProyectos.findById(1L)).thenReturn(Optional.empty());

        when(this.repoProyectos.save(any(Proyecto.class))).thenReturn(new Proyecto());
        when(this.repoTareas.save(any(Tarea.class))).thenReturn(new Tarea());

        Usuario usuarioCreadorDeLaTarea = new Usuario();
        usuarioCreadorDeLaTarea.setUsername("user1");

        ProyectoNotFoundException exception = assertThrows(ProyectoNotFoundException.class, () ->{
            this.tareaService.crearTarea(request,usuarioCreadorDeLaTarea);
        });

        String mensajeEsperado = "No se encontro el proyecto con id: 1";
        String mensajeActual = exception.getMessage();

        assertThat(mensajeActual).isEqualTo(mensajeEsperado);
    }

    @Test
    public void no_se_puede_reasignar_una_tarea_a_un_usuario_no_registrado() {
        TareaRequest request = new TareaRequest();
        request.setTitulo("implementar x cosa");
        request.setFechaLimite(LocalDate.of(2040, Month.SEPTEMBER,20 ));
        request.setEstado(EstadoTarea.PENDIENTE);
        request.setUsername("user6");//no existe el user6
        request.setPrioridad(Prioridad.ALTA);
        request.setIdProyecto(1L);

        when(this.repoProyectos.findById(1L)).thenReturn(Optional.of(new Proyecto()));
        when(this.repoProyectos.save(any(Proyecto.class))).thenReturn(new Proyecto());

        when(this.repoTareas.save(any(Tarea.class))).thenReturn(new Tarea());
        when(this.repoTareas.findById(1L)).thenReturn(Optional.of(new Tarea()));


        Usuario usuarioCreadorDeLaTarea = new Usuario();
        usuarioCreadorDeLaTarea.setUsername("user1");

        UsuarioNotFoundException exception = assertThrows(UsuarioNotFoundException.class, () ->{
            this.tareaService.actializarTarea(1L, request, usuarioCreadorDeLaTarea);
        });

        String mensajeEsperado = "No existe el usuario con el username: 'user6'";
        String mensajeActual = exception.getMessage();

        assertThat(mensajeActual).isEqualTo(mensajeEsperado);
    }

    @Test
    public void no_se_puede_actualizar_una_tarea_para_un_proyecto_no_virgente() {
        TareaRequest request = new TareaRequest();
        request.setTitulo("implementar x cosa");
        request.setFechaLimite(LocalDate.of(2040, Month.SEPTEMBER,20 ));
        request.setEstado(EstadoTarea.PENDIENTE);
        request.setUsername("user3");
        request.setPrioridad(Prioridad.ALTA);
        request.setIdProyecto(1L);

        Proyecto proyectoNoVirgente = new Proyecto();
        proyectoNoVirgente.setProyectoVirgente(false);

        when(this.repoProyectos.findById(1L)).thenReturn(Optional.of(proyectoNoVirgente));
        when(this.repoProyectos.save(any(Proyecto.class))).thenReturn(new Proyecto());

        when(this.repoTareas.save(any(Tarea.class))).thenReturn(new Tarea());
        when(this.repoTareas.findById(1L)).thenReturn(Optional.of(new Tarea()));


        Usuario usuarioCreadorDeLaTarea = new Usuario();
        usuarioCreadorDeLaTarea.setUsername("user1");

        ProyectoNotFoundException exception = assertThrows(ProyectoNotFoundException.class, () ->{
            this.tareaService.actializarTarea(1L, request, usuarioCreadorDeLaTarea);
        });

        String mensajeEsperado = "No se encontro el proyecto con id: 1";
        String mensajeActual = exception.getMessage();

        assertThat(mensajeActual).isEqualTo(mensajeEsperado);
    }


    private void assertTarea(Tarea tarea, TareaRequest request, Usuario usuarioCraedorDeLaTarea){
        assertThat(tarea.getTitulo()).isEqualTo(request.getTitulo());
        assertThat(tarea.getEstado()).isEqualTo(request.getEstado());
        assertThat(tarea.getPrioridad()).isEqualTo(request.getPrioridad());
        assertThat(tarea.getFechaLimite()).isEqualTo(request.getFechaLimite());
        assertThat(tarea.getUsuairoAsignado().getUsername()).isEqualTo(request.getUsername());
        assertThat(tarea.getComentarios().size()).isEqualTo(request.getComentarios().size());

        for(String comentario: request.getComentarios()){
            Mensaje comentarioCreado = new Mensaje();
            comentarioCreado.setMensaje(comentario);
            comentarioCreado.setUsuario(usuarioCraedorDeLaTarea);

            assertThat(tarea.getComentarios()).contains(comentarioCreado);
        }

    }

}
