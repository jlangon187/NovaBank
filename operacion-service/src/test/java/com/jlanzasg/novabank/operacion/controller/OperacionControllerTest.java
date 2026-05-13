package com.jlanzasg.novabank.operacion.controller;

import com.jlanzasg.novabank.operacion.dto.operacion.response.MovimientoResponseDTO;
import com.jlanzasg.novabank.operacion.exception.DuplicateException;
import com.jlanzasg.novabank.operacion.exception.ExchangeRateUnavailableException;
import com.jlanzasg.novabank.operacion.exception.GlobalExceptionHandler;
import com.jlanzasg.novabank.operacion.exception.SaldoInsuficienteException;
import com.jlanzasg.novabank.operacion.service.OperacionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperacionControllerTest {

    private WebTestClient webTestClient;

    @Mock
    private OperacionService operacionService;

    @InjectMocks
    private OperacionController operacionController;

    private void setupClient() {
        this.webTestClient = WebTestClient.bindToController(operacionController)
                .controllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void deposito_ReturnsOk() {
        setupClient();
        MovimientoResponseDTO response = new MovimientoResponseDTO();
        response.setTipoMovimiento("DEPOSITO");
        when(operacionService.depositar(any())).thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/operaciones/deposito")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "cuentaIban": "ES91210000000000000001",
                          "importe": 100.0
                        }
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.tipoMovimiento").isEqualTo("DEPOSITO");
    }

    @Test
    void streamMovimientos_RecibeEventos() {
        setupClient();
        MovimientoResponseDTO m1 = new MovimientoResponseDTO();
        m1.setTipoMovimiento("DEPOSITO");
        MovimientoResponseDTO m2 = new MovimientoResponseDTO();
        m2.setTipoMovimiento("RETIRO");

        when(operacionService.obtenerStreamingMovimientos()).thenReturn(Flux.just(m1, m2));

        FluxExchangeResult<MovimientoResponseDTO> result = webTestClient.get()
                .uri("/operaciones/streaming/movimientos")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .returnResult(MovimientoResponseDTO.class);

        StepVerifier.create(result.getResponseBody())
                .expectNextCount(2)
                .thenCancel()
                .verify();
    }

    @Test
    void deposito_WhenBodyInvalid_Returns400() {
        setupClient();

        webTestClient.post()
                .uri("/operaciones/deposito")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "cuentaIban": "BAD",
                          "importe": 0
                        }
                        """)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Bad Request")
                .jsonPath("$.path").isEqualTo("/operaciones/deposito");
    }

    @Test
    void transferencia_WhenMismaCuenta_Returns409() {
        setupClient();
        when(operacionService.transferir(any())).thenReturn(Flux.error(new DuplicateException("Misma cuenta")));

        webTestClient.post()
                .uri("/operaciones/transferencia")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "monedaOrigen": "EUR",
                          "monedaDestino": "EUR",
                          "cuentaOrigen": "ES91210000000000000001",
                          "cuentaDestino": "ES91210000000000000001",
                          "importe": 10.0
                        }
                        """)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Misma cuenta");
    }

    @Test
    void transferencia_WhenSaldoInsuficiente_Returns409() {
        setupClient();
        when(operacionService.transferir(any())).thenReturn(Flux.error(new SaldoInsuficienteException("Sin saldo")));

        webTestClient.post()
                .uri("/operaciones/transferencia")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "monedaOrigen": "EUR",
                          "monedaDestino": "USD",
                          "cuentaOrigen": "ES91210000000000000001",
                          "cuentaDestino": "ES91210000000000000015",
                          "importe": 1000.0
                        }
                        """)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Sin saldo");
    }

    @Test
    void transferencia_WhenExchangeUnavailable_Returns503() {
        setupClient();
        when(operacionService.transferir(any())).thenReturn(Flux.error(new ExchangeRateUnavailableException("FX caido")));

        webTestClient.post()
                .uri("/operaciones/transferencia")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "monedaOrigen": "USD",
                          "monedaDestino": "EUR",
                          "cuentaOrigen": "ES91210000000000000001",
                          "cuentaDestino": "ES91210000000000000015",
                          "importe": 50.0
                        }
                        """)
                .exchange()
                .expectStatus().isEqualTo(503)
                .expectBody()
                .jsonPath("$.message").isEqualTo("FX caido");
    }

    @Test
    void streamMovimientos_WhenNoEvents_ClosesWithoutItems() {
        setupClient();
        when(operacionService.obtenerStreamingMovimientos()).thenReturn(Flux.empty());

        FluxExchangeResult<MovimientoResponseDTO> result = webTestClient.get()
                .uri("/operaciones/streaming/movimientos")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .returnResult(MovimientoResponseDTO.class);

        StepVerifier.create(result.getResponseBody())
                .verifyComplete();
    }

    @Test
    void obtenerMovimientos_WithDateRange_ReturnsFilteredResult() {
        setupClient();
        MovimientoResponseDTO response = new MovimientoResponseDTO();
        response.setTipoMovimiento("RETIRO");
        response.setFecha(LocalDateTime.of(2026, 5, 10, 12, 0));
        when(operacionService.obtenerMovimientosPorCuentaYFecha(any(), any(), any())).thenReturn(Flux.just(response));

        webTestClient.get()
                .uri("/operaciones/movimientos/ES91210000000000000001?fechaInicio=2026-05-10&fechaFin=2026-05-10")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].tipoMovimiento").isEqualTo("RETIRO")
                .jsonPath("$[0].fecha").exists();
    }
}
