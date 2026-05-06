package com.jlanzasg.novabank.cuenta.integration;

import com.jlanzasg.novabank.cuenta.client.ClienteClient;
import com.jlanzasg.novabank.cuenta.dto.cliente.response.ClienteResponseDTO;
import com.jlanzasg.novabank.cuenta.dto.cuenta.response.CuentaResponseDTO;
import com.jlanzasg.novabank.cuenta.service.CuentaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The type Cuenta integration test.
 */
@SpringBootTest
@ActiveProfiles("test")
class CuentaIntegrationTest {

    @Autowired
    private CuentaService cuentaService;

    @MockitoBean
    private ClienteClient clienteClient;

    /**
     * Crear cuenta persists and returns data.
     */
    @Test
    void crearCuenta_PersistsAndReturnsData() {
        ClienteResponseDTO cliente = new ClienteResponseDTO();
        cliente.setId(77L);
        cliente.setNombre("Laura");
        cliente.setApellidos("Sanz");
        when(clienteClient.getClienteById(77L)).thenReturn(cliente);

        CuentaResponseDTO result = cuentaService.crearCuenta(77L, null);

        assertThat(result.getIban()).startsWith("ES91210000");
        assertThat(result.getClienteId()).isEqualTo(77L);
    }

    /**
     * Crear y buscar cuenta por iban full flow.
     */
    @Test
    void crearYBuscarCuentaPorIban_FullFlow() {
        ClienteResponseDTO cliente = new ClienteResponseDTO();
        cliente.setId(88L);
        cliente.setNombre("Nora");
        cliente.setApellidos("Sierra");
        when(clienteClient.getClienteById(88L)).thenReturn(cliente);

        CuentaResponseDTO creada = cuentaService.crearCuenta(88L, null);
        CuentaResponseDTO encontrada = cuentaService.findAccountByIban(creada.getIban());

        assertThat(encontrada.getIban()).isEqualTo(creada.getIban());
        assertThat(encontrada.getClienteId()).isEqualTo(88L);
    }

    /**
     * Crear cuenta when cliente missing throws not found.
     */
    @Test
    void crearCuenta_WhenClienteMissing_ThrowsNotFound() {
        when(clienteClient.getClienteById(99L)).thenThrow(mock(feign.FeignException.NotFound.class));

        assertThatThrownBy(() -> cuentaService.crearCuenta(99L, null))
                .isInstanceOf(com.jlanzasg.novabank.cuenta.exception.NotFoundException.class);
    }
}
