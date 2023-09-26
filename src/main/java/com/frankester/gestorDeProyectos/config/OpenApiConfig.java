package com.frankester.gestorDeProyectos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI myOpenAPI() {

        Contact contact = new Contact();
        contact.setEmail("francotomascallero@gmail.com");
        contact.setName("Franco Callero");

        License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Gestor de Proyectos API")
                .version("1.0")
                .contact(contact)
                .description("Esta API te permite gestionar proyectos, tareas, archivos y salas de chat asociadas a cada proyecto. Es una herramienta poderosa para colaborar en proyectos y mantener un seguimiento de las actividades y comunicaciones.\n")
                .license(mitLicense);

        return new OpenAPI().info(info);
    }

}
