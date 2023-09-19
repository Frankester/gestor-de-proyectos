package com.frankester.gestorDeProyectos.services.impl;

import com.frankester.gestorDeProyectos.models.Tarea;
import com.frankester.gestorDeProyectos.services.ArchivosService;
import com.frankester.gestorDeProyectos.services.TareaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

@Service
public class ArchivosServiceImpl implements ArchivosService {

    @Autowired
    TareaService tareaService;

    @Autowired
    private S3Client s3;

    @Value("${aws.bucketName}")
    private String BucketName;

    @Override
    public void guardarArchivos(Map<String, MultipartFile> requestFiles, Tarea tarea) throws IOException {

        for(MultipartFile file: requestFiles.values()){
            String fileName = file.getOriginalFilename();

            tarea.addArchivo(fileName);

            guardarArchivoRealEnLaNube(file);
        }

        this.tareaService.guardarTareaModificada(tarea);
    }

    @Override
    public byte[] descargarAchivo(String filename,Tarea tarea) throws IOException {
        if(!existeArchivoConNombre(filename)){
            throw new FileNotFoundException("No existe el archivo con el nombre '" + filename +"'");
        }

        boolean esArchivoDeLaTarea = tarea.getArchivos().contains(filename);

        if(!esArchivoDeLaTarea){
            throw new FileNotFoundException("El archivo '" + filename +"' no pertenece a la tarea "+ tarea.getTitulo());
        }

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(this.BucketName)
                .key(filename)
                .build();

        ResponseInputStream<GetObjectResponse> getObjectResponse = s3.getObject(getObjectRequest);

        return getObjectResponse.readAllBytes();
    }

    private void guardarArchivoRealEnLaNube(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(this.BucketName)
                .key(fileName)
                .build();

        this.s3.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
    }

    private boolean existeArchivoConNombre(String filename){
        try{
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .key(filename)
                    .bucket(this.BucketName)
                    .build();

            this.s3.headObject(headObjectRequest);

            return true;
        } catch(S3Exception exception){
            if(exception.statusCode() == 404){
                return false;
            }
        }
        return false;
    }



}
