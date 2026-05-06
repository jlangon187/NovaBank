package com.jlanzasg.novabank.cliente.integration;

import com.jlanzasg.novabank.cliente.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The type Cliente integration test.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ClienteIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteRepository clienteRepository;

    @BeforeEach
    void cleanDatabase() {
        clienteRepository.deleteAll();
    }

    /**
     * Create and read client full flow.
     *
     * @throws Exception the exception
     */
    @Test
    void createAndReadClient_FullFlow() throws Exception {
        mockMvc.perform(post("/clientes")
                        .contentType(APPLICATION_JSON)
                        .content("{\"dni\":\"87654321Z\",\"nombre\":\"Mario\",\"apellidos\":\"Ruiz\",\"email\":\"mario@test.com\",\"telefono\":\"611222333\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/clientes/dni/87654321Z"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dni").value("87654321Z"));
    }

    /**
     * Delete client after create returns no content and then not found.
     *
     * @throws Exception the exception
     */
    @Test
    void deleteClient_AfterCreate_ReturnsNoContentAndThenNotFound() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/clientes")
                        .contentType(APPLICATION_JSON)
                        .content("{\"dni\":\"11223344X\",\"nombre\":\"Lucia\",\"apellidos\":\"Navas\",\"email\":\"lucia@test.com\",\"telefono\":\"622333444\"}"))
                .andExpect(status().isCreated())
                .andReturn();

        String createdId = com.jayway.jsonpath.JsonPath.read(createResult.getResponse().getContentAsString(), "$.id").toString();

        mockMvc.perform(delete("/clientes/{id}", createdId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/clientes/{id}", createdId))
                .andExpect(status().isNotFound());
    }

    /**
     * Create client when duplicate dni returns conflict.
     *
     * @throws Exception the exception
     */
    @Test
    void createClient_WhenDuplicateDni_ReturnsConflict() throws Exception {
        String body = "{\"dni\":\"44556677M\",\"nombre\":\"Paula\",\"apellidos\":\"Ramos\",\"email\":\"paula1@test.com\",\"telefono\":\"633444555\"}";

        mockMvc.perform(post("/clientes")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/clientes")
                        .contentType(APPLICATION_JSON)
                        .content("{\"dni\":\"44556677M\",\"nombre\":\"Paula\",\"apellidos\":\"Ramos\",\"email\":\"paula2@test.com\",\"telefono\":\"633444556\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"));
    }
}
