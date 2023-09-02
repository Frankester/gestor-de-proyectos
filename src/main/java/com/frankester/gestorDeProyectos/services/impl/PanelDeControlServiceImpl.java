package com.frankester.gestorDeProyectos.services.impl;

import com.frankester.gestorDeProyectos.exceptions.custom.PanelDeControlNotFoundException;
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
    public PanelDeControl acutalizarPanelDeControl(Proyecto proyecto) throws PanelDeControlNotFoundException {
        PanelDeControl panelDeControl = proyecto.getPanelDeControl();

        if(panelDeControl == null){
            throw new PanelDeControlNotFoundException("No se encontro el panel de control para el proyecto '"+proyecto.getNombre()+"'");
        }

        panelDeControl.setProgresoDelProyecto(proyecto.calcularProgresoDelProyecto());
        panelDeControl.setAlertas(proyecto.obtenerAlertasDelProyecto());
        panelDeControl.setTareasPendientes(proyecto.obtenerTareasPendientes());

        return panelDeControl;
    }
}
