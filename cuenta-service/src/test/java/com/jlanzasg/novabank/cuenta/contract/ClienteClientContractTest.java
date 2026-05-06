package com.jlanzasg.novabank.cuenta.contract;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.jlanzasg.novabank.cuenta.client.ClienteClient;
import com.jlanzasg.novabank.cuenta.dto.cliente.response.ClienteResponseDTO;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import com.jlanzasg.novabank.cuenta.client.ClienteServiceFallback;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * The type Cliente client contract test.
 */
@SpringBootTest(classes = ClienteClientContractTest.TestApp.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class ClienteClientContractTest {

    /**
     * The constant wm.
     */
    @RegisterExtension
    static WireMockExtension wm = WireMockExtension.newInstance().options(options().dynamicPort()).build();

    /**
     * Props.
     *
     * @param registry the registry
     */
    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.openfeign.client.config.cliente-service.url", wm::baseUrl);
        registry.add("spring.cloud.openfeign.circuitbreaker.enabled", () -> "false");
        registry.add("eureka.client.enabled", () -> "false");
        registry.add("spring.cloud.config.enabled", () -> "false");
    }

    @Autowired
    private ClienteClient clienteClient;

    /**
     * Gets cliente by id when 200 maps response.
     */
    @Test
    void getClienteById_When200_MapsResponse() {
        wm.stubFor(get(urlEqualTo("/clientes/1"))
                .willReturn(okJson("{\"id\":1,\"dni\":\"12345678A\",\"nombre\":\"Ana\",\"apellidos\":\"Lopez\",\"email\":\"ana@test.com\",\"telefono\":\"600111222\"}")));

        ClienteResponseDTO response = clienteClient.getClienteById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getDni()).isEqualTo("12345678A");
    }

    /**
     * Gets cliente by id when 404 throws feign not found.
     */
    @Test
    void getClienteById_When404_ThrowsFeignNotFound() {
        wm.stubFor(get(urlEqualTo("/clientes/999"))
                .willReturn(aResponse().withStatus(404)));

        assertThatThrownBy(() -> clienteClient.getClienteById(999L))
                .isInstanceOf(FeignException.NotFound.class);
    }

    /**
     * The type Test app.
     */
    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EnableFeignClients(clients = ClienteClient.class)
    @Import(ClienteServiceFallback.class)
    static class TestApp {
    }
}
