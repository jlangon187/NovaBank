package com.jlanzasg.novabank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlanzasg.novabank.dto.operacion.request.OperacionRequestDTO;
import com.jlanzasg.novabank.dto.operacion.request.TransferenciaRequestDTO;
import com.jlanzasg.novabank.dto.operacion.response.MovimientoResponseDTO;
import com.jlanzasg.novabank.exception.SaldoInsuficienteException;
import com.jlanzasg.novabank.service.OperacionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OperacionController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class OperacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OperacionService operacionService;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void deposito_Exito() throws Exception {
        OperacionRequestDTO request = new OperacionRequestDTO();
        request.setIbanCuenta("ES12345678901234567890");
        request.setImporte(100.0);

        when(operacionService.depositar(any())).thenReturn(new MovimientoResponseDTO());

        mockMvc.perform(post("/operaciones/deposito")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void retiro_Exito() throws Exception {
        OperacionRequestDTO request = new OperacionRequestDTO();
        request.setIbanCuenta("ES12345678901234567890");
        request.setImporte(100.0);

        when(operacionService.retirar(any())).thenReturn(new MovimientoResponseDTO());

        mockMvc.perform(post("/operaciones/retiro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void retiro_SaldoInsuficiente() throws Exception {
        OperacionRequestDTO request = new OperacionRequestDTO();
        request.setIbanCuenta("ES12345678901234567890");
        request.setImporte(1000.0);

        when(operacionService.retirar(any())).thenThrow(new SaldoInsuficienteException("Insuficiente"));

        mockMvc.perform(post("/operaciones/retiro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()); // Assuming GlobalExceptionHandler maps this to 400
    }

    @Test
    void transferencia_Exito() throws Exception {
        TransferenciaRequestDTO request = new TransferenciaRequestDTO();
        request.setCuentaOrigen("ES11111111111111111111");
        request.setCuentaDestino("ES22222222222222222222");
        request.setImporte(100.0);

        when(operacionService.transferir(any())).thenReturn(List.of(new MovimientoResponseDTO(), new MovimientoResponseDTO()));

        mockMvc.perform(post("/operaciones/transferencia")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
