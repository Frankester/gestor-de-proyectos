package com.frankester.gestorDeProyectos.models;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
public class Persistence {

    @Id
    @GeneratedValue
    private Long id;
}
