package com.jlanzasg.novabank.operacion.service;

import com.jlanzasg.novabank.operacion.client.ExchangeRateClient;
import com.jlanzasg.novabank.operacion.dto.operacion.request.OperacionRequestDTO;
import com.jlanzasg.novabank.operacion.dto.operacion.request.TransferenciaRequestDTO;
import com.jlanzasg.novabank.operacion.exception.ExchangeRateUnavailableException;
import com.jlanzasg.novabank.operacion.exception.DuplicateException;
import com.jlanzasg.novabank.operacion.exception.SaldoInsuficienteException;
import com.jlanzasg.novabank.operacion.exception.NotFoundException;
import com.jlanzasg.novabank.operacion.mapper.impl.OperacionMapper;
import com.jlanzasg.novabank.operacion.model.Movimiento;
import com.jlanzasg.novabank.operacion.model.TipoMovimiento;
import com.jlanzasg.novabank.operacion.repository.OperacionRepository;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperacionServiceTest {

    @Mock
    private OperacionRepository operacionRepository;
    @Mock
    private ExchangeRateClient exchangeRateClient;

    private TransferenciaRequestDTO transferenciaBase() {
        TransferenciaRequestDTO dto = new TransferenciaRequestDTO();
        dto.setCuentaOrigen("ES91210000000000000001");
        dto.setCuentaDestino("ES91210000000000000015");
        dto.setImporte(100.0);
        dto.setMonedaOrigen("EUR");
        dto.setMonedaDestino("USD");
        return dto;
    }

    @Test
    void depositar_WhenValid_CompletesWithMovimiento() throws IOException {
        try (MockWebServer cuentaServer = new MockWebServer()) {
            cuentaServer.enqueue(new MockResponse()
                    .setHeader("Content-Type", "application/json")
                    .setBody("{\"iban\":\"ES91210000000000000001\",\"balance\":200.0}"));
            cuentaServer.enqueue(new MockResponse().setResponseCode(200));
            cuentaServer.start();

            OperacionMapper mapper = new OperacionMapper();
            OperacionService service = new OperacionService(
                    operacionRepository,
                    WebClient.builder(),
                    exchangeRateClient,
                    mapper,
                    String.format("http://localhost:%s", cuentaServer.getPort())
            );

            when(operacionRepository.save(any(Movimiento.class))).thenAnswer(invocation -> {
                Movimiento mov = invocation.getArgument(0);
                mov.setId(1L);
                mov.setFecha(LocalDateTime.now());
                mov.setTipo(TipoMovimiento.DEPOSITO);
                return Mono.just(mov);
            });

            OperacionRequestDTO request = new OperacionRequestDTO();
            request.setCuentaIban("ES91210000000000000001");
            request.setImporte(100.0);

            StepVerifier.create(service.depositar(request))
                    .expectNextMatches(m -> m.getTipoMovimiento().equals("DEPOSITO") && m.getCantidad().equals(100.0))
                    .verifyComplete();
        }
    }

    @Test
    void retirar_WhenInsufficientBalance_EmitsError() throws IOException {
        try (MockWebServer cuentaServer = new MockWebServer()) {
            cuentaServer.enqueue(new MockResponse()
                    .setHeader("Content-Type", "application/json")
                    .setBody("{\"iban\":\"ES91210000000000000001\",\"balance\":50.0}"));
            cuentaServer.start();

            OperacionService service = new OperacionService(
                    operacionRepository,
                    WebClient.builder(),
                    exchangeRateClient,
                    new OperacionMapper(),
                    String.format("http://localhost:%s", cuentaServer.getPort())
            );

            OperacionRequestDTO request = new OperacionRequestDTO();
            request.setCuentaIban("ES91210000000000000001");
            request.setImporte(500.0);

            StepVerifier.create(service.retirar(request))
                    .expectError(SaldoInsuficienteException.class)
                    .verify();
        }
    }

    @Test
    void transferirEnDivisa_CuandoExchangeFalla_AbortaSinTocarCuentas() throws IOException {
        try (MockWebServer cuentaServer = new MockWebServer()) {
            cuentaServer.start();

            OperacionService service = new OperacionService(
                    operacionRepository,
                    WebClient.builder(),
                    exchangeRateClient,
                    new OperacionMapper(),
                    String.format("http://localhost:%s", cuentaServer.getPort())
            );

            when(exchangeRateClient.obtenerTasaCambioSegura("USD", "EUR"))
                    .thenReturn(Mono.error(new ExchangeRateUnavailableException("USD", "EUR", null)));

            TransferenciaRequestDTO dto = new TransferenciaRequestDTO();
            dto.setCuentaOrigen("ES91210000000000000001");
            dto.setCuentaDestino("ES91210000000000000015");
            dto.setImporte(1000.0);
            dto.setMonedaOrigen("USD");
            dto.setMonedaDestino("EUR");

            StepVerifier.create(service.transferir(dto))
                    .expectError(ExchangeRateUnavailableException.class)
                    .verify();

            verify(operacionRepository, never()).save(any(Movimiento.class));
            org.assertj.core.api.Assertions.assertThat(cuentaServer.getRequestCount()).isEqualTo(0);
        }
    }

    @Test
    void transferir_WhenValid_ActualizaCuentasYGeneraDosMovimientos() throws IOException, InterruptedException {
        try (MockWebServer cuentaServer = new MockWebServer()) {
            cuentaServer.enqueue(new MockResponse()
                    .setHeader("Content-Type", "application/json")
                    .setBody("{\"iban\":\"ES91210000000000000001\",\"balance\":500.0}"));
            cuentaServer.enqueue(new MockResponse()
                    .setHeader("Content-Type", "application/json")
                    .setBody("{\"iban\":\"ES91210000000000000015\",\"balance\":300.0}"));
            cuentaServer.enqueue(new MockResponse().setResponseCode(200));
            cuentaServer.start();

            OperacionService service = new OperacionService(
                    operacionRepository,
                    WebClient.builder(),
                    exchangeRateClient,
                    new OperacionMapper(),
                    String.format("http://localhost:%s", cuentaServer.getPort())
            );

            when(exchangeRateClient.obtenerTasaCambioSegura("EUR", "USD")).thenReturn(Mono.just(1.2));
            when(operacionRepository.save(any(Movimiento.class))).thenAnswer(invocation -> {
                Movimiento mov = invocation.getArgument(0);
                mov.setId(10L);
                mov.setFecha(LocalDateTime.now());
                return Mono.just(mov);
            });

            TransferenciaRequestDTO dto = transferenciaBase();

            StepVerifier.create(service.transferir(dto).collectList())
                    .expectNextMatches(list -> list.size() == 2
                            && "TRANSFERENCIA_SALIENTE".equals(list.get(0).getTipoMovimiento())
                            && "TRANSFERENCIA_ENTRANTE".equals(list.get(1).getTipoMovimiento())
                            && list.get(0).getCantidad().equals(100.0)
                            && list.get(1).getCantidad().equals(120.0))
                    .verifyComplete();

            okhttp3.mockwebserver.RecordedRequest saldoRequest = cuentaServer.takeRequest();
            saldoRequest = cuentaServer.takeRequest();
            saldoRequest = cuentaServer.takeRequest();
            org.assertj.core.api.Assertions.assertThat(saldoRequest.getMethod()).isEqualTo("PUT");
            org.assertj.core.api.Assertions.assertThat(saldoRequest.getPath()).isEqualTo("/cuentas/saldos");
            org.assertj.core.api.Assertions.assertThat(saldoRequest.getBody().readUtf8())
                    .contains("\"ibanOrigen\":\"ES91210000000000000001\"")
                    .contains("\"ibanDestino\":\"ES91210000000000000015\"")
                    .contains("\"nuevoSaldoOrigen\":")
                    .contains("\"nuevoSaldoDestino\":");

            verify(operacionRepository).save(argThat(m -> m.getTipo() == TipoMovimiento.TRANSFERENCIA_SALIENTE));
            verify(operacionRepository).save(argThat(m -> m.getTipo() == TipoMovimiento.TRANSFERENCIA_ENTRANTE));
        }
    }

    @Test
    void transferir_WhenInsufficientBalance_EmitsErrorAndSkipsUpdates() throws IOException {
        try (MockWebServer cuentaServer = new MockWebServer()) {
            cuentaServer.enqueue(new MockResponse()
                    .setHeader("Content-Type", "application/json")
                    .setBody("{\"iban\":\"ES91210000000000000001\",\"balance\":20.0}"));
            cuentaServer.enqueue(new MockResponse()
                    .setHeader("Content-Type", "application/json")
                    .setBody("{\"iban\":\"ES91210000000000000015\",\"balance\":30.0}"));
            cuentaServer.start();

            OperacionService service = new OperacionService(
                    operacionRepository,
                    WebClient.builder(),
                    exchangeRateClient,
                    new OperacionMapper(),
                    String.format("http://localhost:%s", cuentaServer.getPort())
            );

            when(exchangeRateClient.obtenerTasaCambioSegura("EUR", "USD")).thenReturn(Mono.just(1.1));
            TransferenciaRequestDTO dto = transferenciaBase();
            dto.setImporte(100.0);

            StepVerifier.create(service.transferir(dto))
                    .expectError(SaldoInsuficienteException.class)
                    .verify();

            verify(operacionRepository, never()).save(any(Movimiento.class));
            org.assertj.core.api.Assertions.assertThat(cuentaServer.getRequestCount()).isEqualTo(2);
        }
    }

    @Test
    void transferir_WhenSameAccount_EmitsDuplicateException() {
        OperacionService service = new OperacionService(
                operacionRepository,
                WebClient.builder(),
                exchangeRateClient,
                new OperacionMapper(),
                "http://localhost:9999"
        );

        TransferenciaRequestDTO dto = transferenciaBase();
        dto.setCuentaDestino(dto.getCuentaOrigen());

        StepVerifier.create(service.transferir(dto))
                .expectError(DuplicateException.class)
                .verify();

        verify(exchangeRateClient, never()).obtenerTasaCambioSegura(any(), any());
        verify(operacionRepository, never()).save(any(Movimiento.class));
    }

    @Test
    void transferir_WhenImporteNoValido_EmitsIllegalArgumentException() {
        OperacionService service = new OperacionService(
                operacionRepository,
                WebClient.builder(),
                exchangeRateClient,
                new OperacionMapper(),
                "http://localhost:9999"
        );

        TransferenciaRequestDTO dto = transferenciaBase();
        dto.setImporte(0.0);

        StepVerifier.create(service.transferir(dto))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void depositar_WhenImporteNoValido_EmitsIllegalArgumentException() {
        OperacionService service = new OperacionService(
                operacionRepository,
                WebClient.builder(),
                exchangeRateClient,
                new OperacionMapper(),
                "http://localhost:9999"
        );

        OperacionRequestDTO request = new OperacionRequestDTO();
        request.setCuentaIban("ES91210000000000000001");
        request.setImporte(0.0);

        StepVerifier.create(service.depositar(request))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void retirar_WhenImporteNoValido_EmitsIllegalArgumentException() {
        OperacionService service = new OperacionService(
                operacionRepository,
                WebClient.builder(),
                exchangeRateClient,
                new OperacionMapper(),
                "http://localhost:9999"
        );

        OperacionRequestDTO request = new OperacionRequestDTO();
        request.setCuentaIban("ES91210000000000000001");
        request.setImporte(-10.0);

        StepVerifier.create(service.retirar(request))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void depositar_WhenCuentaServiceReturns404_EmitsNotFound() throws IOException {
        try (MockWebServer cuentaServer = new MockWebServer()) {
            cuentaServer.enqueue(new MockResponse().setResponseCode(404));
            cuentaServer.start();

            OperacionService service = new OperacionService(
                    operacionRepository,
                    WebClient.builder(),
                    exchangeRateClient,
                    new OperacionMapper(),
                    String.format("http://localhost:%s", cuentaServer.getPort())
            );

            OperacionRequestDTO request = new OperacionRequestDTO();
            request.setCuentaIban("ES91210000000000000001");
            request.setImporte(10.0);

            StepVerifier.create(service.depositar(request))
                    .expectError(NotFoundException.class)
                    .verify();

            verify(operacionRepository, never()).save(any(Movimiento.class));
        }
    }

    @Test
    void transferir_WhenCuentaUpdateFails_DoesNotPersistMovimientos() throws IOException {
        try (MockWebServer cuentaServer = new MockWebServer()) {
            cuentaServer.enqueue(new MockResponse()
                    .setHeader("Content-Type", "application/json")
                    .setBody("{\"iban\":\"ES91210000000000000001\",\"balance\":500.0}"));
            cuentaServer.enqueue(new MockResponse()
                    .setHeader("Content-Type", "application/json")
                    .setBody("{\"iban\":\"ES91210000000000000015\",\"balance\":300.0}"));
            cuentaServer.enqueue(new MockResponse().setResponseCode(500));
            cuentaServer.start();

            OperacionService service = new OperacionService(
                    operacionRepository,
                    WebClient.builder(),
                    exchangeRateClient,
                    new OperacionMapper(),
                    String.format("http://localhost:%s", cuentaServer.getPort())
            );

            when(exchangeRateClient.obtenerTasaCambioSegura("EUR", "USD")).thenReturn(Mono.just(1.2));

            TransferenciaRequestDTO dto = transferenciaBase();

            StepVerifier.create(service.transferir(dto))
                    .expectError()
                    .verify();
        }
    }
}
