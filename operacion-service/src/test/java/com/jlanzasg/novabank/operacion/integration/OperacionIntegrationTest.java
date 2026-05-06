package com.jlanzasg.novabank.operacion.integration;

import com.jlanzasg.novabank.operacion.client.CuentaClient;
import com.jlanzasg.novabank.operacion.dto.cuenta.response.CuentaResponseDTO;
import com.jlanzasg.novabank.operacion.dto.operacion.request.OperacionRequestDTO;
import com.jlanzasg.novabank.operacion.dto.operacion.request.TransferenciaRequestDTO;
import com.jlanzasg.novabank.operacion.dto.operacion.response.MovimientoResponseDTO;
import com.jlanzasg.novabank.operacion.service.OperacionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

/**
 * The type Operacion integration test.
 */
@SpringBootTest
@ActiveProfiles("test")
class OperacionIntegrationTest {

    @Autowired
    private OperacionService operacionService;

    @MockitoBean
    private CuentaClient cuentaClient;

    /**
     * Depositar full flow with persistence.
     */
    @Test
    void depositar_FullFlowWithPersistence() {
        OperacionRequestDTO request = new OperacionRequestDTO();
        request.setIbanCuenta("ES91210000000000000009");
        request.setImporte(50.0);

        CuentaResponseDTO cuenta = new CuentaResponseDTO();
        cuenta.setIban("ES91210000000000000009");
        cuenta.setBalance(100.0);

        when(cuentaClient.getCuentaByIban("ES91210000000000000009")).thenReturn(cuenta);
        doNothing().when(cuentaClient).actualizarSaldo(eq("ES91210000000000000009"), anyDouble());

        MovimientoResponseDTO result = operacionService.depositar(request);

        assertThat(result.getTipoMovimiento()).isEqualTo("DEPOSITO");
        assertThat(result.getCantidad()).isEqualTo(50.0);
    }

    /**
     * Retirar full flow with persistence.
     */
    @Test
    void retirar_FullFlowWithPersistence() {
        OperacionRequestDTO request = new OperacionRequestDTO();
        request.setIbanCuenta("ES91210000000000000010");
        request.setImporte(40.0);

        CuentaResponseDTO cuenta = new CuentaResponseDTO();
        cuenta.setIban("ES91210000000000000010");
        cuenta.setBalance(100.0);

        when(cuentaClient.getCuentaByIban("ES91210000000000000010")).thenReturn(cuenta);
        doNothing().when(cuentaClient).actualizarSaldo(eq("ES91210000000000000010"), anyDouble());

        MovimientoResponseDTO result = operacionService.retirar(request);

        assertThat(result.getTipoMovimiento()).isEqualTo("RETIRO");
        assertThat(result.getCantidad()).isEqualTo(40.0);
    }

    /**
     * Transferir full flow with persistence.
     */
    @Test
    void transferir_FullFlowWithPersistence() {
        TransferenciaRequestDTO request = new TransferenciaRequestDTO();
        request.setCuentaOrigen("ES91210000000000000011");
        request.setCuentaDestino("ES91210000000000000012");
        request.setImporte(30.0);

        CuentaResponseDTO origen = new CuentaResponseDTO();
        origen.setIban("ES91210000000000000011");
        origen.setBalance(100.0);

        CuentaResponseDTO destino = new CuentaResponseDTO();
        destino.setIban("ES91210000000000000012");
        destino.setBalance(10.0);

        when(cuentaClient.getCuentaByIban("ES91210000000000000011")).thenReturn(origen);
        when(cuentaClient.getCuentaByIban("ES91210000000000000012")).thenReturn(destino);
        doNothing().when(cuentaClient).actualizarSaldo(eq("ES91210000000000000011"), anyDouble());
        doNothing().when(cuentaClient).actualizarSaldo(eq("ES91210000000000000012"), anyDouble());

        List<MovimientoResponseDTO> result = operacionService.transferir(request);

        assertThat(result).hasSize(2);
    }
}
