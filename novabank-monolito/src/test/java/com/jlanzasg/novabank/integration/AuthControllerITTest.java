package com.jlanzasg.novabank.integration;

import com.jlanzasg.novabank.dto.auth.AuthRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerITTest extends BaseControllerIT {

    @Test
    void login_conCredencialesValidas_devuelveToken() throws Exception {
        AuthRequestDTO registerRequest = new AuthRequestDTO();
        registerRequest.setEmail("auth-it@novabank.com");
        registerRequest.setPassword("123456");

        mockMvc.perform(post("/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void login_conPasswordInvalida_devuelve401() throws Exception {
        AuthRequestDTO registerRequest = new AuthRequestDTO();
        registerRequest.setEmail("auth-error-it@novabank.com");
        registerRequest.setPassword("123456");

        mockMvc.perform(post("/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        AuthRequestDTO badLogin = new AuthRequestDTO();
        badLogin.setEmail(registerRequest.getEmail());
        badLogin.setPassword("bad-password");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badLogin)))
                .andExpect(status().isUnauthorized());
    }
}
