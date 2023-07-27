package com.frankester.gestorDeProyectos.models.mensajeria;

import com.frankester.gestorDeProyectos.models.Persistence;
import com.frankester.gestorDeProyectos.models.Usuario;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class ChatRoom extends Persistence {

    @OneToMany
    @JoinColumn(name = "id_chat_room", referencedColumnName = "id")
    private List<Mensaje> mensajes;

    private String nombre;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "administrador", referencedColumnName = "username")
    private Usuario administrador;

    public ChatRoom() {
        this.mensajes = new ArrayList<>();
    }

    public void addMensaje(Mensaje mensajeAGuardar) {
        this.mensajes.add(mensajeAGuardar);
    }
}
