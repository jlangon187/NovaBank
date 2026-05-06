package com.jlanzasg.novabank.integration;

import com.jlanzasg.novabank.dto.cliente.request.ClienteRequestDTO;
import com.jlanzasg.novabank.dto.operacion.request.OperacionRequestDTO;
import com.jlanzasg.novabank.dto.operacion.request.TransferenciaRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OperacionControllerITTest extends BaseControllerIT {

    @Test
    void deposito_valido_devuelve200YMovimiento() throws Exception {
        String token = registerAndLogin();
        String iban = createCuentaForNewCliente(token, "12345678F", "op-it1@novabank.com", "600111240");

        OperacionRequestDTO request = new OperacionRequestDTO();
        request.setIbanCuenta(iban);
        request.setImporte(100.0);

        mockMvc.perform(post("/operaciones/deposito")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idMovimiento").isNumber())
                .andExpect(jsonPath("$.tipoMovimiento").value("DEPOSITO"))
                .andExpect(jsonPath("$.cantidad").value(100.0));
    }

    @Test
    void retiro_sinSaldoSuficiente_devuelve409() throws Exception {
        String token = registerAndLogin();
        String iban = createCuentaForNewCliente(token, "12345678G", "op-it2@novabank.com", "600111241");

        OperacionRequestDTO request = new OperacionRequestDTO();
        request.setIbanCuenta(iban);
        request.setImporte(50.0);

        mockMvc.perform(post("/operaciones/retiro")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void transferencia_valida_devuelveListaDeDosMovimientos() throws Exception {
        String token = registerAndLogin();
        String ibanOrigen = createCuentaForNewCliente(token, "12345678H", "op-it3@novabank.com", "600111242");
        String ibanDestino = createCuentaForNewCliente(token, "12345678J", "op-it4@novabank.com", "600111243");

        OperacionRequestDTO deposito = new OperacionRequestDTO();
        deposito.setIbanCuenta(ibanOrigen);
        deposito.setImporte(200.0);

        mockMvc.perform(post("/operaciones/deposito")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deposito)))
                .andExpect(status().isOk());

        TransferenciaRequestDTO transferencia = new TransferenciaRequestDTO();
        transferencia.setCuentaOrigen(ibanOrigen);
        transferencia.setCuentaDestino(ibanDestino);
        transferencia.setImporte(75.0);

        mockMvc.perform(post("/operaciones/transferencia")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferencia)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipoMovimiento").value("TRANSFERENCIA_SALIENTE"))
                .andExpect(jsonPath("$[1].tipoMovimiento").value("TRANSFERENCIA_ENTRANTE"));
    }

    private String createCuentaForNewCliente(String token, String dni, String email, String telefono) throws Exception {
        Long clienteId = createCliente(token, dni, email, telefono);

        String response = mockMvc.perform(post("/cuentas")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .param("clienteId", String.valueOf(clienteId)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("iban").asText();
    }

    private Long createCliente(String token, String dni, String email, String telefono) throws Exception {
        ClienteRequestDTO request = new ClienteRequestDTO();
        request.setDni(dni);
        request.setNombre("Cliente");
        request.setApellidos("Operacion IT");
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
