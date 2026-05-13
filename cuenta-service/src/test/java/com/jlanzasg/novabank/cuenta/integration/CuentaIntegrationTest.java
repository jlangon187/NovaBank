package com.jlanzasg.novabank.cuenta.integration;

import com.jlanzasg.novabank.cuenta.controller.CuentaController;
import com.jlanzasg.novabank.cuenta.dto.cuenta.response.CuentaResponseDTO;
import com.jlanzasg.novabank.cuenta.exception.GlobalExceptionHandler;
import com.jlanzasg.novabank.cuenta.service.CuentaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CuentaIntegrationTest {

    @Mock
    private CuentaService cuentaService;

    @InjectMocks
    private CuentaController cuentaController;

    @Test
    void getCuentaByIban_ReturnsOk() {
        WebTestClient client = WebTestClient.bindToController(cuentaController)
                .controllerAdvice(new GlobalExceptionHandler())
                .build();

        CuentaResponseDTO response = new CuentaResponseDTO();
        response.setIban("ES91210000000000000001");
        response.setClienteId(77L);
        when(cuentaService.findAccountByIban("ES91210000000000000001")).thenReturn(Mono.just(response));

        client.get()
                .uri("/cuentas/iban/ES91210000000000000001")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.clienteId").isEqualTo(77);
    }
}
