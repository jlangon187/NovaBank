package com.jlanzasg.novabank.integration;

import com.jlanzasg.novabank.dto.cliente.request.ClienteRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CuentaControllerITTest extends BaseControllerIT {

    @Test
    void crearCuenta_conToken_devuelveCuentaConIban() throws Exception {
        String token = registerAndLogin();
        Long clienteId = createCliente(token, "12345678D", "cuenta-it1@novabank.com", "600111230");

        mockMvc.perform(post("/cuentas")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .param("clienteId", String.valueOf(clienteId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.iban").value(org.hamcrest.Matchers.matchesPattern("^ES\\d{20}$")))
                .andExpect(jsonPath("$.balance").value(0.0));
    }

    @Test
    void listarCuentasPorCliente_devuelve200() throws Exception {
        String token = registerAndLogin();
        Long clienteId = createCliente(token, "12345678E", "cuenta-it2@novabank.com", "600111231");

        mockMvc.perform(post("/cuentas")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .param("clienteId", String.valueOf(clienteId)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/cuentas/cliente/{idCliente}", clienteId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].iban").exists())
                .andExpect(jsonPath("$[0].balance").exists());
    }

    @Test
    void buscarCuentaPorIban_inexistente_devuelve404() throws Exception {
        String token = registerAndLogin();

        mockMvc.perform(get("/cuentas/cliente/iban/{iban}", "ES00000000000000000000")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    private Long createCliente(String token, String dni, String email, String telefono) throws Exception {
        ClienteRequestDTO request = new ClienteRequestDTO();
        request.setDni(dni);
        request.setNombre("Cliente");
        request.setApellidos("Cuenta IT");
        request.setEmail(email);
        request.setTelefono(telefono);

        String response = mockMvc.perform(post("/clientes")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }
}
