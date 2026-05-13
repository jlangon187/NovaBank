package com.jlanzasg.novabank.cliente.integration;

import com.jlanzasg.novabank.cliente.controller.ClienteController;
import com.jlanzasg.novabank.cliente.dto.cliente.response.ClienteResponseDTO;
import com.jlanzasg.novabank.cliente.exception.GlobalExceptionHandler;
import com.jlanzasg.novabank.cliente.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteIntegrationTest {

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private ClienteController clienteController;

    @Test
    void createAndReadClient_FullFlow() {
        WebTestClient client = WebTestClient.bindToController(clienteController)
                .controllerAdvice(new GlobalExceptionHandler())
                .build();

        ClienteResponseDTO response = new ClienteResponseDTO();
        response.setId(1L);
        response.setDni("87654321Z");

        when(clienteService.save(any())).thenReturn(Mono.just(response));
        when(clienteService.findByDni("87654321Z")).thenReturn(Mono.just(response));

        client.post()
                .uri("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "dni": "87654321Z",
                          "nombre": "Mario",
                          "apellidos": "Ruiz",
                          "email": "mario@test.com",
                          "telefono": "611222333"
                        }
                        """)
                .exchange()
                .expectStatus().isCreated();

        client.get()
                .uri("/clientes/dni/87654321Z")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.dni").isEqualTo("87654321Z");
    }
}
