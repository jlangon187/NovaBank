package com.jlanzasg.novabank;

import com.jlanzasg.novabank.dto.cliente.request.ClienteRequestDTO;
import com.jlanzasg.novabank.dto.cliente.response.ClienteResponseDTO;
import com.jlanzasg.novabank.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class NovaBankIntegrationTest {

    @Autowired
    private ClienteService clienteService;

    @Test
    void contextLoads() {
    }

    @Test
    void testCrearClienteIntegracion() {
        ClienteRequestDTO request = new ClienteRequestDTO();
        request.setDni("11111111H");
        request.setNombre("Integration");
        request.setApellidos("Test");
        request.setEmail("integration@test.com");
        request.setTelefono("666777888");

        ClienteResponseDTO response = clienteService.save(request);

        assertNotNull(response.getId());
    }
}
