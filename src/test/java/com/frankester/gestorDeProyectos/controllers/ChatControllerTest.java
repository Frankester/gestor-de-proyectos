package com.frankester.gestorDeProyectos.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frankester.gestorDeProyectos.controllers.securityTestConfig.TestMessageChannel;
import com.frankester.gestorDeProyectos.controllers.securityTestConfig.WithMockUser;
import com.frankester.gestorDeProyectos.exceptions.custom.ProyectoNotFoundException;
import com.frankester.gestorDeProyectos.models.Proyecto;
import com.frankester.gestorDeProyectos.models.Usuario;
import com.frankester.gestorDeProyectos.models.mensajeria.ChatRoom;
import com.frankester.gestorDeProyectos.models.mensajeria.Mensaje;
import com.frankester.gestorDeProyectos.models.mensajeria.MensajeDeChat;
import com.frankester.gestorDeProyectos.services.ChatService;
import com.frankester.gestorDeProyectos.services.ProyectoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.messaging.support.MessageBuilder;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ChatControllerTest {


    @MockBean
    private ChatService chatService;

    @MockBean
    private ProyectoService proyectoService;

    private ChatController chatController;

    @MockBean
    S3Client s3;

    private TestMessageChannel clientOutboundChannel;

    private TestAnnotationMethodHandler annotationMethodHandler;



    @BeforeEach
    public void setup(){
        this.clientOutboundChannel = new TestMessageChannel();
        this.chatController = new ChatController(this.chatService, this.proyectoService);

        this.annotationMethodHandler = new TestAnnotationMethodHandler(
                new TestMessageChannel(), clientOutboundChannel, new SimpMessagingTemplate(new TestMessageChannel()));

        this.annotationMethodHandler.registerHandler(this.chatController);
        this.annotationMethodHandler.setDestinationPrefixes(List.of("/project"));
        this.annotationMethodHandler.setMessageConverter(new MappingJackson2MessageConverter());
        this.annotationMethodHandler.setApplicationContext(new StaticApplicationContext());
        this.annotationMethodHandler.afterPropertiesSet();


    }


    @WithMockUser
    @Test
    public void usuarios_pueden_enviar_mensajes_a_miembros_del_proyecto() throws ExecutionException, InterruptedException, TimeoutException, ProyectoNotFoundException, JsonProcessingException {
        Usuario usuario = this.mockUsuario();
        Proyecto proyecto = this.mockProyecto(usuario);
        String mensajeAEnviar = "Holaaaa!!!";

        when(this.proyectoService.obtenerProyectoConId(anyLong())).thenReturn(proyecto);



        MensajeDeChat mensajeDeChat = new MensajeDeChat();
        mensajeDeChat.setUsername(usuario.getUsername());
        mensajeDeChat.setMensaje(mensajeAEnviar);

        byte[] payload =new ObjectMapper().writeValueAsBytes(mensajeDeChat);

        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.MESSAGE);
        headers.setDestination("/project/chat/"+1L);
        headers.setSessionId("0");
        headers.setUser(usuario::getUsername);
        headers.setSessionAttributes(new HashMap<>());

        org.springframework.messaging.Message<byte[]> message = MessageBuilder
                .withPayload(payload)
                .setHeaders(headers)
                .build();

        this.annotationMethodHandler.handleMessage(message);


        Mensaje mensaje = new Mensaje();
        mensaje.setUsuario(usuario);
        mensaje.setMensaje(mensajeAEnviar);

        assertThat(proyecto.getSalaDeChat().getMensajes()).contains(mensaje);
        verify(this.chatService, times(1)).sendMessageToUsers(ArgumentMatchers.any(MensajeDeChat.class),anyList());

    }


    private Proyecto mockProyecto(Usuario usuario){

        Proyecto mockProyecto = new Proyecto();
        mockProyecto.addMiembro(usuario);
        mockProyecto.setSalaDeChat(new ChatRoom());

        return mockProyecto;
    }

    private Usuario mockUsuario(){
        Usuario mockUsuario = new Usuario();
        mockUsuario.setUsername("pepe");
        return mockUsuario;
    }


    private static class TestAnnotationMethodHandler extends SimpAnnotationMethodMessageHandler {

        public TestAnnotationMethodHandler(SubscribableChannel inChannel, MessageChannel outChannel,
                                           SimpMessageSendingOperations brokerTemplate) {

            super(inChannel, outChannel, brokerTemplate);
        }

        public void registerHandler(Object handler) {
            super.detectHandlerMethods(handler);
        }
    }

}
