package com.frankester.gestorDeProyectos.controllers.securityTestConfig;

import com.frankester.gestorDeProyectos.models.Usuario;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.ArrayList;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockUser> {
    @Override
    public SecurityContext createSecurityContext(WithMockUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Usuario principal = new Usuario();
        principal.setUsername(customUser.username());

        Authentication auth =
                new UsernamePasswordAuthenticationToken(principal, customUser.password(), new ArrayList<>());
        context.setAuthentication(auth);

        return context;
    }
}
