package com.frankester.gestorDeProyectos.services;

import com.frankester.gestorDeProyectos.exceptions.custom.ProyectoNotFoundException;
import com.frankester.gestorDeProyectos.models.PanelDeControl;
import com.frankester.gestorDeProyectos.models.Proyecto;
import com.frankester.gestorDeProyectos.services.impl.PanelDeControlServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PanelDeControlTest {

    @TestConfiguration
    static class PanelDeControlTestContextConfiguration{
        @Bean
        public PanelDeControlService panelDeControlService(){
            return new PanelDeControlServiceImpl();
        }
    }

    @Autowired
    private PanelDeControlService panelDeControlService;

    @MockBean
    S3Client s3;

    @Test
    public void cuando_creo_un_panel_de_control_debe_devolver_el_panel_de_control_creado(){

        Proyecto proyectoMock = mock(Proyecto.class);

        when(proyectoMock.calcularProgresoDelProyecto()).thenReturn(12F);
        when(proyectoMock.obtenerAlertasDelProyecto()).thenReturn(new ArrayList<>());
        when(proyectoMock.obtenerTareasPendientes()).thenReturn(new ArrayList<>());

        PanelDeControl panelDeControlCreado = this.panelDeControlService.crearPanelDeControl(proyectoMock);

        assertThat(panelDeControlCreado.getProgresoDelProyecto()).isEqualTo(12F);
        assertThat(panelDeControlCreado.getAlertas().size()).isEqualTo(0);
        assertThat(panelDeControlCreado.getTareasPendientes().size()).isEqualTo(0);
    }


    @Test
    public void cuando_actualizo_un_panel_de_control_debe_devolver_el_panel_de_control_actualizado(){

        Proyecto proyectoMock = mock(Proyecto.class);

        when(proyectoMock.calcularProgresoDelProyecto()).thenReturn(12F);
        when(proyectoMock.obtenerAlertasDelProyecto()).thenReturn(new ArrayList<>());
        when(proyectoMock.obtenerTareasPendientes()).thenReturn(new ArrayList<>());
        when(proyectoMock.getPanelDeControl()).thenReturn(new PanelDeControl());


        PanelDeControl panelDeControlActualizado = this.panelDeControlService.acutalizarPanelDeControl(proyectoMock);

        assertThat(panelDeControlActualizado.getProgresoDelProyecto()).isEqualTo(12F);
        assertThat(panelDeControlActualizado.getAlertas().size()).isEqualTo(0);
        assertThat(panelDeControlActualizado.getTareasPendientes().size()).isEqualTo(0);
    }

}
