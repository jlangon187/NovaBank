package com.jlanzasg.novabank.operacion.controller;

import com.jlanzasg.novabank.operacion.dto.operacion.response.MovimientoResponseDTO;
import com.jlanzasg.novabank.operacion.exception.DuplicateException;
import com.jlanzasg.novabank.operacion.exception.NotFoundException;
import com.jlanzasg.novabank.operacion.exception.SaldoInsuficienteException;
import com.jlanzasg.novabank.operacion.exception.ServiceException;
import com.jlanzasg.novabank.operacion.service.OperacionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

/**
 * The type Operacion controller test.
 */
@WebMvcTest(controllers = OperacionController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class OperacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OperacionService operacionService;

    /**
     * Deposito returns ok.
     *
     * @throws Exception the exception
     */
    @Test
    void deposito_ReturnsOk() throws Exception {
        when(operacionService.depositar(any())).thenReturn(new MovimientoResponseDTO());

        mockMvc.perform(post("/operaciones/deposito")
                        .contentType(APPLICATION_JSON)
                        .content("{\"ibanCuenta\":\"ES91210000000000000001\",\"importe\":100.0}"))
                .andExpect(status().isOk());
    }

    /**
     * Deposito when invalid request returns bad request.
     *
     * @throws Exception the exception
     */
    @Test
    void deposito_WhenInvalidRequest_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/operaciones/deposito")
                        .contentType(APPLICATION_JSON)
                        .content("{\"ibanCuenta\":\"BAD\",\"importe\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.error").value("Bad Request"));
    }

    /**
     * Retiro when saldo insuficiente returns conflict payload.
     *
     * @throws Exception the exception
     */
    @Test
    void retiro_WhenSaldoInsuficiente_ReturnsConflictPayload() throws Exception {
        when(operacionService.retirar(any())).thenThrow(new SaldoInsuficienteException("Fondos insuficientes"));

        mockMvc.perform(post("/operaciones/retiro")
                        .contentType(APPLICATION_JSON)
                        .content("{\"ibanCuenta\":\"ES91210000000000000001\",\"importe\":100.0}"))
                .andExpect(status().isConflict())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.error").value("Conflict"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.message").value("Fondos insuficientes"));
    }

    /**
     * Obtener saldo when cuenta no existe returns not found payload.
     *
     * @throws Exception the exception
     */
    @Test
    void obtenerSaldo_WhenCuentaNoExiste_ReturnsNotFoundPayload() throws Exception {
        when(operacionService.consultarSaldo("ES404")).thenThrow(new NotFoundException("Cuenta no encontrada"));

        mockMvc.perform(get("/operaciones/saldo/ES404"))
                .andExpect(status().isNotFound())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.error").value("Not Found"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.message").value("Cuenta no encontrada"));
    }

    /**
     * Transferencia when same account returns conflict payload.
     *
     * @throws Exception the exception
     */
    @Test
    void transferencia_WhenSameAccount_ReturnsConflictPayload() throws Exception {
        when(operacionService.transferir(any())).thenThrow(new DuplicateException("No se puede realizar una transferencia a la misma cuenta."));

        mockMvc.perform(post("/operaciones/transferencia")
                        .contentType(APPLICATION_JSON)
                        .content("{\"cuentaOrigen\":\"ES91210000000000000001\",\"cuentaDestino\":\"ES91210000000000000001\",\"importe\":10.0}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"));
    }

    /**
     * Obtener movimientos with date filters returns ok.
     *
     * @throws Exception the exception
     */
    @Test
    void obtenerMovimientos_WithDateFilters_ReturnsOk() throws Exception {
        MovimientoResponseDTO movimiento = new MovimientoResponseDTO();
        movimiento.setTipoMovimiento("DEPOSITO");
        when(operacionService.obtenerMovimientosPorCuentaYFecha(any(), any(), any())).thenReturn(List.of(movimiento));

        mockMvc.perform(get("/operaciones/movimientos/ES1")
                        .param("fechaInicio", "2026-01-01")
                        .param("fechaFin", "2026-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipoMovimiento").value("DEPOSITO"));
    }

    /**
     * Obtener movimientos when invalid date format returns bad request.
     *
     * @throws Exception the exception
     */
    @Test
    void obtenerMovimientos_WhenInvalidDateFormat_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/operaciones/movimientos/ES1")
                        .param("fechaInicio", "01-01-2026"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    /**
     * Deposito when service fails returns internal server error payload.
     *
     * @throws Exception the exception
     */
    @Test
    void deposito_WhenServiceFails_ReturnsServiceUnavailablePayload() throws Exception {
        when(operacionService.depositar(any())).thenThrow(new ServiceException("Cuenta service no disponible"));

        mockMvc.perform(post("/operaciones/deposito")
                        .contentType(APPLICATION_JSON)
                        .content("{\"ibanCuenta\":\"ES91210000000000000001\",\"importe\":100.0}"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").value("Service Unavailable"))
                .andExpect(jsonPath("$.status").value(503));
    }
}
