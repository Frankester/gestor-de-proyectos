package com.frankester.gestorDeProyectos.controllers;

import com.frankester.gestorDeProyectos.exceptions.custom.ProyectoNotFoundException;
import com.frankester.gestorDeProyectos.exceptions.custom.TareaNotFoundException;
import com.frankester.gestorDeProyectos.exceptions.custom.UserAlreadyExistsException;
import com.frankester.gestorDeProyectos.exceptions.custom.UsuarioNotFoundException;
import com.frankester.gestorDeProyectos.models.DTOs.AuthDTO;
import com.frankester.gestorDeProyectos.models.DTOs.TareaRequest;
import com.frankester.gestorDeProyectos.models.Proyecto;
import com.frankester.gestorDeProyectos.models.Tarea;
import com.frankester.gestorDeProyectos.models.Usuario;
import com.frankester.gestorDeProyectos.models.estados.EstadoTarea;
import com.frankester.gestorDeProyectos.models.estados.Prioridad;
import com.frankester.gestorDeProyectos.services.ArchivosService;
import com.frankester.gestorDeProyectos.services.TareaService;
import com.frankester.gestorDeProyectos.services.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TareasControllerTest {

    @MockBean
    public TareaService tareaService;

    @MockBean
    public UsuarioService userService;

    @MockBean
    public ArchivosService archivosService;

    @Autowired
    public TareasController tareasController;

    @MockBean
    S3Client s3;

    @Test
    public void cuando_un_usuario_crea_una_tarea_se_debe_llamar_al_servicio_correcto() throws ProyectoNotFoundException, UsuarioNotFoundException {
        TareaRequest request = this.mockTareaRequest();

        Usuario userOwner = new Usuario();
        userOwner.setUsername("pepe2");

        when(this.userService.obtenerUsuarioAutenticado()).thenReturn(userOwner);

        Tarea newTarea = new Tarea();
        when(this.tareaService.crearTarea(request, userOwner)).thenReturn(newTarea);

        ResponseEntity<?> responseEntity = this.tareasController.crearTarea(request);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(responseEntity.getBody()).isEqualTo(newTarea);
    }

    @Test
    public void cuando_un_usuario_actualiza_una_tarea_se_debe_llamar_al_servicio_correcto() throws ProyectoNotFoundException, UsuarioNotFoundException, TareaNotFoundException {
        TareaRequest request = this.mockTareaRequest();

        Usuario userOwner = new Usuario();
        userOwner.setUsername("pepe2");

        when(this.userService.obtenerUsuarioAutenticado()).thenReturn(userOwner);

        Tarea newTarea = new Tarea();
        when(tareaService.actializarTarea(2L, request, userOwner)).thenReturn(newTarea);

        ResponseEntity<?> responseEntity = this.tareasController.actualizarTarea(2L, request);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(responseEntity.getBody()).isEqualTo(newTarea);
    }

    @Test
    public void cuando_un_usuario_borra_una_tarea_se_debe_llamar_al_servicio_correcto() throws TareaNotFoundException {

        Tarea tareaToDelete = new Tarea();
        tareaToDelete.setTitulo("Implementar x funcionalidad");
        tareaToDelete.setProyecto(new Proyecto());
        tareaToDelete.getProyecto().setNombre("The Manhattan Project");

        when(this.tareaService.obtenerTareaConId(1L)).thenReturn(tareaToDelete);

        ResponseEntity<?> responseEntity = this.tareasController.borrarTarea(1L);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(responseEntity.getBody()).isEqualTo("Se elimino la tarea " + tareaToDelete.getTitulo() + " correctamente. para el proyecto '" +tareaToDelete.getProyecto().getNombre()+ "'");
    }

    @Test
    public void cuando_un_usuario_guarda_un_archivo_para_una_tarea_se_debe_llamar_al_servicio_correcto() throws TareaNotFoundException, IOException {

        MultipartFile multipartFile = mock(MultipartFile.class);

        Tarea tareaToUpdate = new Tarea();
        tareaToUpdate.setTitulo("Implementar x funcionalidad");

        when(this.tareaService.obtenerTareaConId(1L)).thenReturn(tareaToUpdate);

        ResponseEntity<?> responseEntity = this.tareasController.guardarArchivoNuevo(1L, Map.of("megafile.pdf",multipartFile));

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(responseEntity.getBody()).isEqualTo("Se agregaron 1 archivos para la tarea '" +tareaToUpdate.getTitulo()+ "' con exito");
    }

    @Test
    public void cuando_un_usuario_descarga_un_archivo_para_una_tarea_se_debe_llamar_al_servicio_correcto() throws TareaNotFoundException, IOException {

        MultipartFile multipartFile = mock(MultipartFile.class);

        Tarea tareaToUpdate = new Tarea();
        tareaToUpdate.setTitulo("Implementar x funcionalidad");

        when(this.tareaService.obtenerTareaConId(1L)).thenReturn(tareaToUpdate);

        byte[] bytesDelArchivo = {0,1,8,2};
        when(this.archivosService.descargarAchivo("megafile.pdf", tareaToUpdate)).thenReturn(bytesDelArchivo);

        ResponseEntity<?> responseEntity = this.tareasController.descargarArchivo(1L, "megafile.pdf");

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(responseEntity.getHeaders().getContentDisposition()).isEqualTo(ContentDisposition.builder("attachment").filename("megafile.pdf").build());

        assertThat(responseEntity.getBody()).isEqualTo(new ByteArrayResource(bytesDelArchivo));


    }

    private TareaRequest mockTareaRequest(){
        TareaRequest request = new TareaRequest();
        request.setUsername("pepe");
        request.setTitulo("Implementar x funcionalidad");
        request.setDescripcion("Debe funcionar de X manera y sobretodo no debe tener Y");
        request.setPrioridad(Prioridad.ALTA);
        request.setEstado(EstadoTarea.PENDIENTE);
        request.setIdProyecto(1L);
        request.setComentarios(List.of("No hagas ...", "Puedes apoyarte de x recurso"));
        request.setFechaLimite(LocalDate.of(2023, 10, 12));

        return request;
    }

}
