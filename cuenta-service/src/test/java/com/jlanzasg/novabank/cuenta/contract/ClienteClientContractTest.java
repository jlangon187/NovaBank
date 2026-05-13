package com.jlanzasg.novabank.cuenta.contract;

import com.jlanzasg.novabank.cuenta.dto.cuenta.response.CuentaResponseDTO;
import com.jlanzasg.novabank.cuenta.dto.cuenta.request.CuentaRequestDTO;
import com.jlanzasg.novabank.cuenta.mapper.impl.CuentaMapper;
import com.jlanzasg.novabank.cuenta.model.Cuenta;
import com.jlanzasg.novabank.cuenta.repository.CuentaRepository;
import com.jlanzasg.novabank.cuenta.service.CuentaService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteClientContractTest {

    @Mock
    private CuentaRepository cuentaRepository;

    @Test
    void crearCuenta_UsesClienteServiceHttpResponse() throws IOException, InterruptedException {
        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(new MockResponse()
                    .setHeader("Content-Type", "application/json")
                    .setBody("{\"id\":1,\"dni\":\"12345678A\",\"nombre\":\"Ana\",\"apellidos\":\"Lopez\",\"email\":\"ana@test.com\",\"telefono\":\"600111222\"}"));
            server.start();

            when(cuentaRepository.obtenerUltimoId()).thenReturn(Mono.just(0L));
            when(cuentaRepository.save(any(Cuenta.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

            CuentaService service = new CuentaService(
                    cuentaRepository,
                    WebClient.builder(),
                    new CuentaMapper(),
                    String.format("http://localhost:%s", server.getPort())
            );

            StepVerifier.create(service.crearCuenta(1L, new CuentaRequestDTO()))
                    .expectNextMatches(c -> c.getClienteId().equals(1L) && c.getIban().equals("ES91210000000000000001"))
                    .verifyComplete();

            okhttp3.mockwebserver.RecordedRequest request = server.takeRequest();
            org.assertj.core.api.Assertions.assertThat(request.getMethod()).isEqualTo("GET");
            org.assertj.core.api.Assertions.assertThat(request.getPath()).isEqualTo("/clientes/1");
        }
    }
}
