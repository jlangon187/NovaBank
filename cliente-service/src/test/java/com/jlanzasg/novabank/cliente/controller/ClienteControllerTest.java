package com.jlanzasg.novabank.cliente.controller;

import com.jlanzasg.novabank.cliente.dto.cliente.response.ClienteResponseDTO;
import com.jlanzasg.novabank.cliente.exception.DuplicateException;
import com.jlanzasg.novabank.cliente.exception.NotFoundException;
import com.jlanzasg.novabank.cliente.exception.GlobalExceptionHandler;
import com.jlanzasg.novabank.cliente.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {

    private WebTestClient webTestClient;

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private ClienteController clienteController;

    private void setupClient() {
        this.webTestClient = WebTestClient.bindToController(clienteController)
                .controllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void findById_WhenExists_Returns200() {
        setupClient();
        ClienteResponseDTO response = new ClienteResponseDTO();
        response.setId(1L);
        response.setDni("12345678A");
        when(clienteService.findById(1L)).thenReturn(Mono.just(response));

        webTestClient.get()
                .uri("/clientes/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ClienteResponseDTO.class)
                .value(dto -> org.assertj.core.api.Assertions.assertThat(dto.getDni()).isEqualTo("12345678A"));
    }

    @Test
    void findById_WhenNotFound_Returns404() {
        setupClient();
        when(clienteService.findById(99L)).thenReturn(Mono.error(new NotFoundException("Cliente no encontrado")));

        webTestClient.get()
                .uri("/clientes/99")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void listClientes_ReturnsArray() {
        setupClient();
        ClienteResponseDTO c1 = new ClienteResponseDTO();
        c1.setId(1L);
        c1.setDni("12345678A");

        when(clienteService.findAll()).thenReturn(Flux.just(c1));

        webTestClient.get()
                .uri("/clientes")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].dni").isEqualTo("12345678A");
    }

    @Test
    void saveCliente_WhenBodyInvalid_Returns400() {
        setupClient();

        webTestClient.post()
                .uri("/clientes")
                .header("Content-Type", "application/json")
                .bodyValue("""
                        {
                          "dni": "BAD",
                          "nombre": "A",
                          "apellidos": "",
                          "email": "wrong",
                          "telefono": "123"
                        }
                        """)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Bad Request")
                .jsonPath("$.path").isEqualTo("/clientes");
    }

    @Test
    void saveCliente_WhenDuplicateConflict_Returns409() {
        setupClient();
        when(clienteService.save(org.mockito.ArgumentMatchers.any()))
                .thenReturn(Mono.error(new DuplicateException("DNI duplicado")));

        webTestClient.post()
                .uri("/clientes")
                .header("Content-Type", "application/json")
                .bodyValue("""
                        {
                          "dni": "12345678A",
                          "nombre": "Mario",
                          "apellidos": "Ruiz",
                          "email": "mario@test.com",
                          "telefono": "611222333"
                        }
                        """)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.message").isEqualTo("DNI duplicado");
    }

    @Test
    void deleteCliente_WhenNotFound_Returns404() {
        setupClient();
        when(clienteService.deleteById(777L)).thenReturn(Mono.error(new NotFoundException("No existe")));

        webTestClient.delete()
                .uri("/clientes/777")
                .exchange()
                .expectStatus().isNotFound();
    }
}
