package com.frankester.gestorDeProyectos.services.impl;

import com.frankester.gestorDeProyectos.models.PanelDeControl;
import com.frankester.gestorDeProyectos.models.Proyecto;
import com.frankester.gestorDeProyectos.services.PanelDeControlService;
import org.springframework.stereotype.Service;

@Service
public class PanelDeControlServiceImpl implements PanelDeControlService {
    @Override
    public PanelDeControl crearPanelDeControl(Proyecto proyecto) {
        PanelDeControl panelDeControl = new PanelDeControl();

        panelDeControl.setProgresoDelProyecto(proyecto.calcularProgresoDelProyecto());
        panelDeControl.setAlertas(proyecto.obtenerAlertasDelProyecto());
        panelDeControl.setTareasPendientes(proyecto.obtenerTareasPendientes());

        return panelDeControl;
    }

    @Override
    public PanelDeControl acutalizarPanelDeControl(Proyecto proyecto) {
        PanelDeControl panelDeControl = proyecto.getPanelDeControl();

        panelDeControl.setProgresoDelProyecto(proyecto.calcularProgresoDelProyecto());
        panelDeControl.setAlertas(proyecto.obtenerAlertasDelProyecto());
        panelDeControl.setTareasPendientes(proyecto.obtenerTareasPendientes());

        return panelDeControl;
    }
}
