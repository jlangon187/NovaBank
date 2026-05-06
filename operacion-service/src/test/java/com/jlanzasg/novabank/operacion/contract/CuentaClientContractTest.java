package com.jlanzasg.novabank.operacion.contract;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.jlanzasg.novabank.operacion.client.CuentaClient;
import com.jlanzasg.novabank.operacion.dto.cuenta.response.CuentaResponseDTO;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * The type Cuenta client contract test.
 */
@SpringBootTest(classes = CuentaClientContractTest.TestApp.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class CuentaClientContractTest {

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
        registry.add("spring.cloud.openfeign.client.config.cuenta-service.url", wm::baseUrl);
        registry.add("spring.cloud.openfeign.circuitbreaker.enabled", () -> "false");
        registry.add("eureka.client.enabled", () -> "false");
        registry.add("spring.cloud.config.enabled", () -> "false");
    }

    @Autowired
    private CuentaClient cuentaClient;

    /**
     * Gets cuenta by iban when 200 maps response.
     */
    @Test
    void getCuentaByIban_When200_MapsResponse() {
        wm.stubFor(get(urlEqualTo("/cuentas/iban/ES91210000000000000001"))
                .willReturn(okJson("{\"id\":1,\"iban\":\"ES91210000000000000001\",\"balance\":400.0,\"clienteId\":2}")));

        CuentaResponseDTO response = cuentaClient.getCuentaByIban("ES91210000000000000001");

        assertThat(response.getIban()).isEqualTo("ES91210000000000000001");
        assertThat(response.getBalance()).isEqualTo(400.0);
    }

    /**
     * Gets cuenta by iban when 404 throws feign not found.
     */
    @Test
    void getCuentaByIban_When404_ThrowsFeignNotFound() {
        wm.stubFor(get(urlEqualTo("/cuentas/iban/ES404"))
                .willReturn(aResponse().withStatus(404)));

        assertThatThrownBy(() -> cuentaClient.getCuentaByIban("ES404"))
                .isInstanceOf(FeignException.NotFound.class);
    }

    /**
     * Actualizar saldo when 500 throws feign exception.
     */
    @Test
    void actualizarSaldo_When500_ThrowsFeignException() {
        wm.stubFor(put(urlPathEqualTo("/cuentas/iban/ES91210000000000000001/saldo"))
                .willReturn(aResponse().withStatus(500)));

        assertThatThrownBy(() -> cuentaClient.actualizarSaldo("ES91210000000000000001", 100.0))
                .isInstanceOf(FeignException.InternalServerError.class);
    }

    /**
     * The type Test app.
     */
    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EnableFeignClients(clients = CuentaClient.class)
    static class TestApp {
    }
}
