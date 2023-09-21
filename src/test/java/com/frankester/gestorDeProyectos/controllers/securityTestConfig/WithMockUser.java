package com.frankester.gestorDeProyectos.controllers.securityTestConfig;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockUser {

    String username() default "pepe";

    String password() default "1234";
}
