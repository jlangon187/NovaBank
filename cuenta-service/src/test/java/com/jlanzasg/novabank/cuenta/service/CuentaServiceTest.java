package com.jlanzasg.novabank.cuenta.service;

import com.jlanzasg.novabank.cuenta.dto.cliente.response.ClienteResponseDTO;
import com.jlanzasg.novabank.cuenta.dto.cuenta.request.ActualizarSaldosRequestDTO;
import com.jlanzasg.novabank.cuenta.dto.cuenta.request.CuentaRequestDTO;
import com.jlanzasg.novabank.cuenta.dto.cuenta.response.CuentaResponseDTO;
import com.jlanzasg.novabank.cuenta.dto.cuenta.response.CuentaSimpleResponseDTO;
import com.jlanzasg.novabank.cuenta.exception.NotFoundException;
import com.jlanzasg.novabank.cuenta.mapper.impl.CuentaMapper;
import com.jlanzasg.novabank.cuenta.model.Cuenta;
import com.jlanzasg.novabank.cuenta.repository.CuentaRepository;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CuentaServiceTest {

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private CuentaMapper cuentaMapper;

    private CuentaService cuentaService;

    @Mock
    private WebClient.Builder webClientBuilder;

    @BeforeEach
    void setUp() {
        WebClient webClient = WebClient.builder().build();
        when(webClientBuilder.baseUrl("http://cliente-service")).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        cuentaService = new CuentaService(cuentaRepository, webClientBuilder, cuentaMapper);
    }

    @Test
    void findAccountsByClientId_WhenExists_ReturnsFlux() throws IOException {
        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(new MockResponse()
                    .setHeader("Content-Type", "application/json")
                    .setBody("{\"id\":1,\"dni\":\"12345678A\",\"nombre\":\"Ana\",\"apellidos\":\"Lopez\",\"email\":\"ana@test.com\",\"telefono\":\"600111222\"}"));
            server.start();

            CuentaRepository repo = org.mockito.Mockito.mock(CuentaRepository.class);
            CuentaMapper mapper = org.mockito.Mockito.mock(CuentaMapper.class);
            CuentaService service = new CuentaService(
                    repo,
                    WebClient.builder(),
                    mapper,
                    String.format("http://localhost:%s", server.getPort())
            );

            Cuenta cuenta = Cuenta.builder().id(1L).iban("ES91210000000000000001").balance(100.0).clienteId(1L).build();
            CuentaSimpleResponseDTO dto = new CuentaSimpleResponseDTO();
            dto.setIban("ES91210000000000000001");

            when(repo.findAllByClienteId(1L)).thenReturn(Flux.just(cuenta));
            when(mapper.toSimpleResponseDTO(cuenta)).thenReturn(dto);

            StepVerifier.create(service.findAccountsByClientId(1L))
                    .expectNextMatches(res -> res.getIban().equals("ES91210000000000000001"))
                    .verifyComplete();
        }
    }

    @Test
    void findAccountsByClientId_WhenClienteNoExiste_EmitsNotFound() throws IOException {
        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(new MockResponse().setResponseCode(404));
            server.start();

            CuentaRepository repo = org.mockito.Mockito.mock(CuentaRepository.class);
            CuentaService service = new CuentaService(
                    repo,
                    WebClient.builder(),
                    new CuentaMapper(),
                    String.format("http://localhost:%s", server.getPort())
            );

            StepVerifier.create(service.findAccountsByClientId(99L))
                    .expectError(NotFoundException.class)
                    .verify();

            verify(repo, org.mockito.Mockito.never()).findAllByClienteId(99L);
        }
    }

    @Test
    void actualizarSaldo_WhenCuentaMissing_EmitsNotFound() {
        when(cuentaRepository.findByIban("ES404")).thenReturn(Mono.empty());

        StepVerifier.create(cuentaService.actualizarSaldo("ES404", 99.0))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void generarIban_WhenLastIdPresent_ReturnsExpectedFormat() {
        when(cuentaRepository.obtenerUltimoId()).thenReturn(Mono.just(15L));

        StepVerifier.create(cuentaService.generarIban())
                .expectNext("ES91210000000000000016")
                .verifyComplete();
    }

    @Test
    void actualizarSaldo_WhenCuentaExists_SavesAndCompletes() {
        Cuenta cuenta = Cuenta.builder().iban("ES91210000000000000001").balance(20.0).clienteId(2L).build();
        when(cuentaRepository.findByIban(cuenta.getIban())).thenReturn(Mono.just(cuenta));
        when(cuentaRepository.save(cuenta)).thenReturn(Mono.just(cuenta));

        StepVerifier.create(cuentaService.actualizarSaldo(cuenta.getIban(), 80.0))
                .verifyComplete();

        verify(cuentaRepository).save(cuenta);
    }

    @Test
    void crearCuenta_WhenClienteExiste_CreaCuentaConIbanYNombre() throws IOException {
        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(new MockResponse()
                    .setHeader("Content-Type", "application/json")
                    .setBody("{\"id\":9,\"dni\":\"12345678A\",\"nombre\":\"Ana\",\"apellidos\":\"Lopez\",\"email\":\"ana@test.com\",\"telefono\":\"600111222\"}"));
            server.start();

            CuentaRepository repo = org.mockito.Mockito.mock(CuentaRepository.class);
            CuentaService service = new CuentaService(
                    repo,
                    WebClient.builder(),
                    new CuentaMapper(),
                    String.format("http://localhost:%s", server.getPort())
            );

            when(repo.obtenerUltimoId()).thenReturn(Mono.just(41L));
            when(repo.save(org.mockito.ArgumentMatchers.any(Cuenta.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

            CuentaRequestDTO req = new CuentaRequestDTO();
            req.setClienteId(9L);

            StepVerifier.create(service.crearCuenta(9L, req))
                    .expectNextMatches(res -> res.getClienteId().equals(9L)
                            && res.getIban().equals("ES91210000000000000042")
                            && "Ana Lopez".equals(res.getClienteName()))
                    .verifyComplete();
        }
    }

    @Test
    void crearCuenta_WhenClienteNoExiste_EmitsNotFound() throws IOException {
        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(new MockResponse().setResponseCode(404));
            server.start();

            CuentaRepository repo = org.mockito.Mockito.mock(CuentaRepository.class);
            CuentaService service = new CuentaService(
                    repo,
                    WebClient.builder(),
                    new CuentaMapper(),
                    String.format("http://localhost:%s", server.getPort())
            );

            CuentaRequestDTO req = new CuentaRequestDTO();
            req.setClienteId(99L);

            StepVerifier.create(service.crearCuenta(99L, req))
                    .expectError(NotFoundException.class)
                    .verify();

            verify(repo, org.mockito.Mockito.never()).save(org.mockito.ArgumentMatchers.any(Cuenta.class));
        }
    }

    @Test
    void findAccountByIban_WhenExists_CombinaCuentaYCliente() throws IOException {
        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(new MockResponse()
                    .setHeader("Content-Type", "application/json")
                    .setBody("{\"id\":7,\"dni\":\"12345678A\",\"nombre\":\"Luis\",\"apellidos\":\"Diaz\",\"email\":\"luis@test.com\",\"telefono\":\"600000001\"}"));
            server.start();

            CuentaRepository repo = org.mockito.Mockito.mock(CuentaRepository.class);
            Cuenta cuenta = Cuenta.builder().id(4L).iban("ES91210000000000000004").balance(25.0).clienteId(7L).build();
            when(repo.findByIban("ES91210000000000000004")).thenReturn(Mono.just(cuenta));

            CuentaService service = new CuentaService(
                    repo,
                    WebClient.builder(),
                    new CuentaMapper(),
                    String.format("http://localhost:%s", server.getPort())
            );

            StepVerifier.create(service.findAccountByIban("ES91210000000000000004"))
                    .expectNextMatches(res -> res.getClienteId().equals(7L)
                            && res.getIban().equals("ES91210000000000000004")
                            && "Luis Diaz".equals(res.getClienteName()))
                    .verifyComplete();
        }
    }

    @Test
    void actualizarSaldos_WhenAmbasCuentasExisten_ActualizaLasDos() {
        Cuenta origen = Cuenta.builder().iban("ES1").balance(100.0).clienteId(1L).build();
        Cuenta destino = Cuenta.builder().iban("ES2").balance(200.0).clienteId(2L).build();
        when(cuentaRepository.findByIban("ES1")).thenReturn(Mono.just(origen));
        when(cuentaRepository.findByIban("ES2")).thenReturn(Mono.just(destino));
        when(cuentaRepository.save(origen)).thenReturn(Mono.just(origen));
        when(cuentaRepository.save(destino)).thenReturn(Mono.just(destino));

        ActualizarSaldosRequestDTO req = new ActualizarSaldosRequestDTO();
        req.setIbanOrigen("ES1");
        req.setNuevoSaldoOrigen(40.0);
        req.setIbanDestino("ES2");
        req.setNuevoSaldoDestino(260.0);

        StepVerifier.create(cuentaService.actualizarSaldos(req))
                .verifyComplete();

        org.assertj.core.api.Assertions.assertThat(origen.getBalance()).isEqualTo(40.0);
        org.assertj.core.api.Assertions.assertThat(destino.getBalance()).isEqualTo(260.0);
        verify(cuentaRepository).save(origen);
        verify(cuentaRepository).save(destino);
    }

    @Test
    void actualizarSaldos_WhenCuentaOrigenNoExiste_EmitsNotFound() {
        when(cuentaRepository.findByIban("ES404")).thenReturn(Mono.empty());
        when(cuentaRepository.findByIban("ES2")).thenReturn(Mono.just(Cuenta.builder().iban("ES2").build()));

        ActualizarSaldosRequestDTO req = new ActualizarSaldosRequestDTO();
        req.setIbanOrigen("ES404");
        req.setIbanDestino("ES2");
        req.setNuevoSaldoOrigen(0.0);
        req.setNuevoSaldoDestino(0.0);

        StepVerifier.create(cuentaService.actualizarSaldos(req))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void actualizarSaldos_WhenCuentaDestinoNoExiste_EmitsNotFound() {
        Cuenta origen = Cuenta.builder().iban("ES1").balance(100.0).clienteId(1L).build();
        when(cuentaRepository.findByIban("ES1")).thenReturn(Mono.just(origen));
        when(cuentaRepository.findByIban("ES404")).thenReturn(Mono.empty());

        ActualizarSaldosRequestDTO req = new ActualizarSaldosRequestDTO();
        req.setIbanOrigen("ES1");
        req.setIbanDestino("ES404");
        req.setNuevoSaldoOrigen(10.0);
        req.setNuevoSaldoDestino(20.0);

        StepVerifier.create(cuentaService.actualizarSaldos(req))
                .expectError(NotFoundException.class)
                .verify();

        verify(cuentaRepository, org.mockito.Mockito.never()).save(org.mockito.ArgumentMatchers.any(Cuenta.class));
    }

    @Test
    void generarIban_WhenNoRows_StartsAtOne() {
        when(cuentaRepository.obtenerUltimoId()).thenReturn(Mono.empty());

        StepVerifier.create(cuentaService.generarIban())
                .expectNext("ES91210000000000000001")
                .verifyComplete();
    }
}
