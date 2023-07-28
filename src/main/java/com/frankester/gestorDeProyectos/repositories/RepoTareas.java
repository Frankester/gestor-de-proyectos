package com.frankester.gestorDeProyectos.repositories;

import com.frankester.gestorDeProyectos.models.Proyecto;
import com.frankester.gestorDeProyectos.models.Tarea;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(path = "tareas")
public interface RepoTareas extends JpaRepository<Tarea, Long> {

    @RestResource(path = "encontrarPorTitulo", rel = "buscarPorTitulo")
    Page<Proyecto> findByTitulo(Pageable page, String titulo);

    @RestResource(exported = false)
    @Override
    void deleteById(Long id);

    @RestResource(exported = false)
    @Override
    void delete(Tarea tarea);

    @RestResource(exported = false)
    @Override
    <S extends Tarea> S save(S entity);
}
