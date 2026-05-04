package com.jlanzasg.novabank.controller;

import com.jlanzasg.novabank.dto.cuenta.response.CuentaResponseDTO;
import com.jlanzasg.novabank.dto.cuenta.response.CuentaSimpleResponseDTO;
import com.jlanzasg.novabank.exception.NotFoundException;
import com.jlanzasg.novabank.service.CuentaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import java.util.Set;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import com.jlanzasg.novabank.config.SecurityConfig;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = CuentaController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class CuentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CuentaService cuentaService;

    @Test
    void crearCuenta_Exito() throws Exception {
        CuentaResponseDTO response = new CuentaResponseDTO();
        response.setIban("ES123");
        when(cuentaService.crearCuenta(anyLong())).thenReturn(response);

        mockMvc.perform(post("/cuentas").param("clienteId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.iban").value("ES123"));
    }

    @Test
    void findByClienteId_Exito() throws Exception {
        when(cuentaService.findAccountsByClientId(anyLong())).thenReturn(Set.of(new CuentaSimpleResponseDTO()));

        mockMvc.perform(get("/cuentas/cliente/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void findByIban_Exito() throws Exception {
        CuentaResponseDTO response = new CuentaResponseDTO();
        response.setIban("ES123");
        when(cuentaService.findAccountByIban(anyString())).thenReturn(response);

        mockMvc.perform(get("/cuentas/cliente/iban/ES123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.iban").value("ES123"));
    }

    @Test
    void findByIban_NoEncontrado() throws Exception {
        when(cuentaService.findAccountByIban(anyString())).thenThrow(new NotFoundException("Not found"));

        mockMvc.perform(get("/cuentas/cliente/iban/ES123"))
                .andExpect(status().isNotFound());
    }
}
