package com.jlanzasg.novabank.integration;

import com.jlanzasg.novabank.dto.cliente.request.ClienteRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ClienteControllerITTest extends BaseControllerIT {

    @Test
    void crearCliente_sinToken_devuelve401() throws Exception {
        ClienteRequestDTO request = buildClienteRequest("12345678A", "unauth@novabank.com", "600111222");

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void crearCliente_conToken_devuelve201YPermiteBuscarPorId() throws Exception {
        String token = registerAndLogin();
        ClienteRequestDTO request = buildClienteRequest("12345678B", "cliente-it@novabank.com", "600111223");

        String response = mockMvc.perform(post("/clientes")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.dni").value("12345678B"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/clientes/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.dni").value("12345678B"));
    }

    @Test
    void crearCliente_dniDuplicado_devuelve409() throws Exception {
        String token = registerAndLogin();
        ClienteRequestDTO first = buildClienteRequest("12345678C", "first@novabank.com", "600111224");
        ClienteRequestDTO duplicate = buildClienteRequest("12345678C", "second@novabank.com", "600111225");

        mockMvc.perform(post("/clientes")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(first)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/clientes")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("El DNI 12345678C ya existe en la base de datos"));
    }

    private ClienteRequestDTO buildClienteRequest(String dni, String email, String telefono) {
        ClienteRequestDTO request = new ClienteRequestDTO();
        request.setDni(dni);
        request.setNombre("Cliente");
        request.setApellidos("Integracion");
        request.setEmail(email);
        request.setTelefono(telefono);
        return request;
    }
}
