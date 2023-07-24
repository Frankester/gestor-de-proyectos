package com.frankester.gestorDeProyectos.repositories;

import com.frankester.gestorDeProyectos.models.Proyecto;
import com.frankester.gestorDeProyectos.models.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(path="usuarios")
public interface RepoUsuarios extends JpaRepository<Usuario, String> {

    Page<Usuario> findByProyectosNombre(Pageable page, String nombreDelProyecto);

    Usuario findByTareasTitulo(String tituloDeLaTarea);

    @RestResource(exported = false)
    @Override
    void deleteById(String nombreDeUsuario);

    @RestResource(exported = false)
    @Override
    void delete(Usuario usuario);
}