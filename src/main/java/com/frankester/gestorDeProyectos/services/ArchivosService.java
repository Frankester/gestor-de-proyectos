package com.frankester.gestorDeProyectos.services;

import com.frankester.gestorDeProyectos.models.Tarea;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface ArchivosService {

    public void guardarArchivos(Map<String, MultipartFile> requestFiles, Tarea tarea) throws IOException;

    public byte[] descargarAchivo(String filename,Tarea tarea) throws IOException;

}
