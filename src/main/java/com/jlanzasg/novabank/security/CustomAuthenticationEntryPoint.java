package com.jlanzasg.novabank.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         org.springframework.security.core.AuthenticationException ex)
            throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        String json = """
            {
                "status": 401,
                "error": "Unauthorized",
                "message": "No estás autenticado o el token es inválido.",
                "path": "%s"
            }
            """.formatted(request.getRequestURI());

        response.getWriter().write(json);
    }
}