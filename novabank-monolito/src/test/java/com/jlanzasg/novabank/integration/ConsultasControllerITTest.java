package com.jlanzasg.novabank.integration;

import com.jlanzasg.novabank.dto.cliente.request.ClienteRequestDTO;
import com.jlanzasg.novabank.dto.operacion.request.OperacionRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ConsultasControllerITTest extends BaseControllerIT {

    @Test
    void consultarSaldo_devuelve200YBalanceActual() throws Exception {
        String token = registerAndLogin();
        CuentaData cuenta = createCuentaForNewCliente(token, "12345678K", "con-it1@novabank.com", "600111250");

        OperacionRequestDTO deposito = new OperacionRequestDTO();
        deposito.setIbanCuenta(cuenta.iban());
        deposito.setImporte(125.0);

        mockMvc.perform(post("/operaciones/deposito")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deposito)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/consultas/saldo/{iban}", cuenta.iban())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(125.0));
    }

    @Test
    void consultarMovimientosPorCuenta_devuelve200YLista() throws Exception {
        String token = registerAndLogin();
        CuentaData cuenta = createCuentaForNewCliente(token, "12345678L", "con-it2@novabank.com", "600111251");

        OperacionRequestDTO deposito = new OperacionRequestDTO();
        deposito.setIbanCuenta(cuenta.iban());
        deposito.setImporte(50.0);

        mockMvc.perform(post("/operaciones/deposito")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deposito)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/consultas/{id}/movimientos", cuenta.id())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipoMovimiento").exists())
                .andExpect(jsonPath("$[0].cantidad").value(50.0));
    }

    @Test
    void consultarSaldo_ibanInexistente_devuelve404() throws Exception {
        String token = registerAndLogin();

        mockMvc.perform(get("/consultas/saldo/{iban}", "ES00000000000000000000")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    private CuentaData createCuentaForNewCliente(String token, String dni, String email, String telefono) throws Exception {
        Long clienteId = createCliente(token, dni, email, telefono);

        String response = mockMvc.perform(post("/cuentas")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .param("clienteId", String.valueOf(clienteId)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long cuentaId = objectMapper.readTree(response).get("id").asLong();
        String iban = objectMapper.readTree(response).get("iban").asText();
        return new CuentaData(cuentaId, iban);
    }

    private Long createCliente(String token, String dni, String email, String telefono) throws Exception {
        ClienteRequestDTO request = new ClienteRequestDTO();
        request.setDni(dni);
        request.setNombre("Cliente");
        request.setApellidos("Consulta IT");
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

    private record CuentaData(Long id, String iban) {
    }
}
