package com.jlanzasg.novabank.cuenta.controller;

import com.jlanzasg.novabank.cuenta.dto.cuenta.response.CuentaResponseDTO;
import com.jlanzasg.novabank.cuenta.dto.cuenta.response.CuentaSimpleResponseDTO;
import com.jlanzasg.novabank.cuenta.exception.NotFoundException;
import com.jlanzasg.novabank.cuenta.exception.ServiceException;
import com.jlanzasg.novabank.cuenta.service.CuentaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The type Cuenta controller test.
 */
@WebMvcTest(controllers = CuentaController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class CuentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CuentaService cuentaService;

    /**
     * Crear cuenta returns ok.
     *
     * @throws Exception the exception
     */
    @Test
    void crearCuenta_ReturnsOk() throws Exception {
        CuentaResponseDTO response = new CuentaResponseDTO();
        response.setIban("ES91210000000000000001");
        when(cuentaService.crearCuenta(anyLong(), any())).thenReturn(response);

        mockMvc.perform(post("/cuentas").param("clienteId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.iban").value("ES91210000000000000001"));
    }

    /**
     * Crear cuenta when missing cliente id returns bad request.
     *
     * @throws Exception the exception
     */
    @Test
    void crearCuenta_WhenMissingClienteId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/cuentas"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.path").value("/cuentas"));
    }

    /**
     * Actualizar saldo when missing nuevo saldo returns bad request payload.
     *
     * @throws Exception the exception
     */
    @Test
    void actualizarSaldo_WhenMissingNuevoSaldo_ReturnsBadRequestPayload() throws Exception {
        mockMvc.perform(put("/cuentas/iban/ES91210000000000000001/saldo"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    /**
     * Find by iban when not found returns not found payload.
     *
     * @throws Exception the exception
     */
    @Test
    void findByIban_WhenNotFound_ReturnsNotFoundPayload() throws Exception {
        when(cuentaService.findAccountByIban("ES404")).thenThrow(new NotFoundException("Cuenta no encontrada"));

        mockMvc.perform(get("/cuentas/iban/ES404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Cuenta no encontrada"));
    }

    /**
     * Find by cliente id returns ok.
     *
     * @throws Exception the exception
     */
    @Test
    void findByClienteId_ReturnsOk() throws Exception {
        CuentaSimpleResponseDTO cuenta = new CuentaSimpleResponseDTO();
        cuenta.setIban("ES91210000000000000001");
        cuenta.setBalance(100.0);
        when(cuentaService.findAccountsByClientId(1L)).thenReturn(java.util.Set.of(cuenta));

        mockMvc.perform(get("/cuentas/cliente/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].iban").value("ES91210000000000000001"));
    }

    /**
     * Actualizar saldo returns ok.
     *
     * @throws Exception the exception
     */
    @Test
    void actualizarSaldo_ReturnsOk() throws Exception {
        doNothing().when(cuentaService).actualizarSaldo("ES91210000000000000001", 300.0);

        mockMvc.perform(put("/cuentas/iban/ES91210000000000000001/saldo").param("nuevoSaldo", "300.0"))
                .andExpect(status().isOk());
    }

    /**
     * Find by iban when service fails returns internal server error payload.
     *
     * @throws Exception the exception
     */
    @Test
    void findByIban_WhenServiceFails_ReturnsInternalServerErrorPayload() throws Exception {
        when(cuentaService.findAccountByIban("ES500")).thenThrow(new ServiceException("Dependencia no disponible"));

        mockMvc.perform(get("/cuentas/iban/ES500"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Service Error"))
                .andExpect(jsonPath("$.status").value(500));
    }
}
