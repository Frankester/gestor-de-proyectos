package com.frankester.gestorDeProyectos.repositories;

import com.frankester.gestorDeProyectos.models.Proyecto;
import com.frankester.gestorDeProyectos.models.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Optional;

@RepositoryRestResource(path="usuarios", exported = false)
public interface RepoUsuarios extends JpaRepository<Usuario, String> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByUsername(String username);

    Boolean existsByUsername(String username);

    Page<Usuario> findByProyectosNombre(Pageable page, String nombreDelProyecto);

    Usuario findByTareasTitulo(String tituloDeLaTarea);

    @RestResource(exported = false)
    @Override
    void deleteById(String nombreDeUsuario);

    @RestResource(exported = false)
    @Override
    void delete(Usuario usuario);
}