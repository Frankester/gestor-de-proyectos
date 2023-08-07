package com.frankester.gestorDeProyectos.services;

import com.frankester.gestorDeProyectos.models.PanelDeControl;
import com.frankester.gestorDeProyectos.models.Proyecto;

public interface PanelDeControlService {
    public PanelDeControl crearPanelDeControl(Proyecto proyecto);

    public PanelDeControl acutalizarPanelDeControl(Proyecto proyecto);
}
