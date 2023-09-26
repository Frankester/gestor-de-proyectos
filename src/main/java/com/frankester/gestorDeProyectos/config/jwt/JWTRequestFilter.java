package com.frankester.gestorDeProyectos.config.jwt;

import com.frankester.gestorDeProyectos.services.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTRequestFilter extends OncePerRequestFilter {
    @Autowired
    private UsuarioService userService;

    @Autowired
    private JWTUtils jwtUtil;

    private final Logger log = LoggerFactory.getLogger(JWTRequestFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try{
            String jwt = extractJwt(request);

            if(jwt != null && jwtUtil.isJwtValid(jwt)){
                String username = jwtUtil.getUsernameFromJwt(jwt);

                UserDetails user = this.userService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        user,null,user.getAuthorities()
                );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                log.info("Usuario logeado correctamente");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e){
            log.error(e.getMessage());
        }


        filterChain.doFilter(request, response);
    }


    private String extractJwt(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            return null;
        }

        String jwtFromRequest = authHeader.split("Bearer ")[1];

        return jwtFromRequest;
    }

}
