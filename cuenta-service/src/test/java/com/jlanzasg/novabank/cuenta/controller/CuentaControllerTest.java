package com.jlanzasg.novabank.cuenta.controller;

import com.jlanzasg.novabank.cuenta.dto.cuenta.response.CuentaResponseDTO;
import com.jlanzasg.novabank.cuenta.dto.cuenta.response.CuentaSimpleResponseDTO;
import com.jlanzasg.novabank.cuenta.exception.GlobalExceptionHandler;
import com.jlanzasg.novabank.cuenta.exception.NotFoundException;
import com.jlanzasg.novabank.cuenta.exception.ServiceException;
import com.jlanzasg.novabank.cuenta.service.CuentaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CuentaControllerTest {

    private WebTestClient webTestClient;

    @Mock
    private CuentaService cuentaService;

    @InjectMocks
    private CuentaController cuentaController;

    private void setupClient() {
        this.webTestClient = WebTestClient.bindToController(cuentaController)
                .controllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void findByIban_WhenExists_Returns200() {
        setupClient();
        CuentaResponseDTO response = new CuentaResponseDTO();
        response.setIban("ES91210000000000000001");
        when(cuentaService.findAccountByIban("ES91210000000000000001")).thenReturn(Mono.just(response));

        webTestClient.get()
                .uri("/cuentas/iban/ES91210000000000000001")
                .exchange()
                .expectStatus().isOk()
                .expectBody(CuentaResponseDTO.class)
                .value(dto -> org.assertj.core.api.Assertions.assertThat(dto.getIban()).isEqualTo("ES91210000000000000001"));
    }

    @Test
    void findByIban_WhenMissing_Returns404() {
        setupClient();
        when(cuentaService.findAccountByIban("ES404")).thenReturn(Mono.error(new NotFoundException("No existe")));

        webTestClient.get()
                .uri("/cuentas/iban/ES404")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void actualizarSaldo_WhenValid_Returns200() {
        setupClient();
        when(cuentaService.actualizarSaldo("ES91210000000000000001", 200.0)).thenReturn(Mono.empty());

        webTestClient.put()
                .uri(uriBuilder -> uriBuilder.path("/cuentas/iban/ES91210000000000000001/saldo").queryParam("nuevoSaldo", 200.0).build())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void crearCuenta_WhenMissingClienteIdParam_Returns400() {
        setupClient();

        webTestClient.post()
                .uri("/cuentas")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Bad Request")
                .jsonPath("$.path").isEqualTo("/cuentas");
    }

    @Test
    void actualizarSaldo_WhenMissingNuevoSaldoParam_Returns400() {
        setupClient();

        webTestClient.put()
                .uri("/cuentas/iban/ES91210000000000000001/saldo")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void findByIban_WhenUnexpectedServiceError_Returns503() {
        setupClient();
        when(cuentaService.findAccountByIban("ES500")).thenReturn(Mono.error(new ServiceException("Downstream caido")));

        webTestClient.get()
                .uri("/cuentas/iban/ES500")
                .exchange()
                .expectStatus().isEqualTo(503)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Downstream caido");
    }

    @Test
    void findByClienteId_WhenClienteMissing_Returns404() {
        setupClient();
        when(cuentaService.findAccountsByClientId(99L)).thenReturn(Flux.error(new NotFoundException("No se encontró el cliente con ID: 99")));

        webTestClient.get()
                .uri("/cuentas/cliente/99")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("No se encontró el cliente con ID: 99");
    }

    @Test
    void findByClienteId_WhenClienteExistsWithoutAccounts_ReturnsEmptyList() {
        setupClient();
        when(cuentaService.findAccountsByClientId(1L)).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/cuentas/cliente/1")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CuentaSimpleResponseDTO.class)
                .hasSize(0);
    }
}
