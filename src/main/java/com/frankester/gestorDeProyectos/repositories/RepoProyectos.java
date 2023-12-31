package com.frankester.gestorDeProyectos.repositories;

import com.frankester.gestorDeProyectos.models.Proyecto;
import com.frankester.gestorDeProyectos.models.Tarea;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(path = "proyectos")
@SecurityRequirement(name = "bearerAuth")
public interface RepoProyectos extends JpaRepository<Proyecto, Long> {

    @RestResource(path = "encontrarPorNombre", rel = "buscarPorNombre")
    Page<Proyecto> findByNombre(Pageable page, String nombre);

    @RestResource(exported = false)
    @Override
    void deleteById(Long id);

    @RestResource(exported = false)
    @Override
    void delete(Proyecto proyecto);

    @RestResource(exported = false)
    @Override
    <S extends Proyecto> S save(S entity);
}
