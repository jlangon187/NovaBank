package com.jlanzasg.novabank.controller;

import com.jlanzasg.novabank.dto.cuenta.response.CuentaSaldoResponseDTO;
import com.jlanzasg.novabank.dto.operacion.response.MovimientoResponseDTO;
import com.jlanzasg.novabank.exception.NotFoundException;
import com.jlanzasg.novabank.service.ConsultaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultasController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ConsultasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConsultaService consultaService;

    @Test
    void obtenerMovimientos_Exito() throws Exception {
        when(consultaService.obtenerMovimientosPorCuentaYFecha(anyLong(), any(), any()))
                .thenReturn(List.of(new MovimientoResponseDTO()));

        mockMvc.perform(get("/consultas/1/movimientos")
                        .param("fechaInicio", "2023-01-01")
                        .param("fechaFin", "2023-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void obtenerSaldo_Exito() throws Exception {
        CuentaSaldoResponseDTO saldoDTO = new CuentaSaldoResponseDTO();
        saldoDTO.setBalance(100.0);
        when(consultaService.consultarSaldo(anyString())).thenReturn(saldoDTO);

        mockMvc.perform(get("/consultas/saldo/ES123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(100.0));
    }

    @Test
    void obtenerSaldo_NoEncontrado() throws Exception {
        when(consultaService.consultarSaldo(anyString())).thenThrow(new NotFoundException("Not found"));

        mockMvc.perform(get("/consultas/saldo/ES123"))
                .andExpect(status().isNotFound());
    }
}
