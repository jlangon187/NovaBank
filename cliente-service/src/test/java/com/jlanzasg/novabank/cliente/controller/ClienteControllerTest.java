package com.jlanzasg.novabank.cliente.controller;

import com.jlanzasg.novabank.cliente.dto.cliente.request.ClienteRequestDTO;
import com.jlanzasg.novabank.cliente.dto.cliente.response.ClienteResponseDTO;
import com.jlanzasg.novabank.cliente.exception.DuplicateException;
import com.jlanzasg.novabank.cliente.exception.NotFoundException;
import com.jlanzasg.novabank.cliente.exception.ServiceException;
import com.jlanzasg.novabank.cliente.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.doNothing;

/**
 * The type Cliente controller test.
 */
@WebMvcTest(controllers = ClienteController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClienteService clienteService;

    /**
     * Save cliente returns created.
     *
     * @throws Exception the exception
     */
    @Test
    void saveCliente_ReturnsCreated() throws Exception {
        ClienteResponseDTO response = new ClienteResponseDTO();
        response.setId(1L);
        response.setDni("12345678A");

        when(clienteService.save(any(ClienteRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/clientes")
                        .contentType(APPLICATION_JSON)
                        .content("{\"dni\":\"12345678A\",\"nombre\":\"Juan\",\"apellidos\":\"Perez\",\"email\":\"juan@test.com\",\"telefono\":\"600111222\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dni").value("12345678A"));
    }

    /**
     * Save cliente when invalid request returns bad request.
     *
     * @throws Exception the exception
     */
    @Test
    void saveCliente_WhenInvalidRequest_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/clientes")
                        .contentType(APPLICATION_JSON)
                        .content("{\"dni\":\"BAD\",\"nombre\":\"A\",\"apellidos\":\"\",\"email\":\"bad\",\"telefono\":\"123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.path").value("/clientes"));
    }

    /**
     * Save cliente when duplicate dni returns conflict payload.
     *
     * @throws Exception the exception
     */
    @Test
    void saveCliente_WhenDuplicateDni_ReturnsConflictPayload() throws Exception {
        when(clienteService.save(any(ClienteRequestDTO.class))).thenThrow(new DuplicateException("DNI duplicado"));

        mockMvc.perform(post("/clientes")
                        .contentType(APPLICATION_JSON)
                        .content("{\"dni\":\"12345678A\",\"nombre\":\"Juan\",\"apellidos\":\"Perez\",\"email\":\"juan@test.com\",\"telefono\":\"600111222\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("DNI duplicado"));
    }

    /**
     * Find by id when not found returns not found payload.
     *
     * @throws Exception the exception
     */
    @Test
    void findById_WhenNotFound_ReturnsNotFoundPayload() throws Exception {
        when(clienteService.findById(99L)).thenThrow(new NotFoundException("Cliente no encontrado"));

        mockMvc.perform(get("/clientes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Cliente no encontrado"));
    }

    /**
     * Listar clientes returns ok with array.
     *
     * @throws Exception the exception
     */
    @Test
    void listarClientes_ReturnsOkWithArray() throws Exception {
        ClienteResponseDTO cliente = new ClienteResponseDTO();
        cliente.setId(1L);
        cliente.setDni("12345678A");
        when(clienteService.findAll()).thenReturn(List.of(cliente));

        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].dni").value("12345678A"));
    }

    /**
     * Delete cliente returns no content.
     *
     * @throws Exception the exception
     */
    @Test
    void deleteCliente_ReturnsNoContent() throws Exception {
        doNothing().when(clienteService).deleteById(1L);

        mockMvc.perform(delete("/clientes/1"))
                .andExpect(status().isNoContent());
    }

    /**
     * Find by dni when service fails returns internal server error payload.
     *
     * @throws Exception the exception
     */
    @Test
    void findByDni_WhenServiceFails_ReturnsInternalServerErrorPayload() throws Exception {
        when(clienteService.findByDni("12345678A")).thenThrow(new ServiceException("Fallo de dependencia"));

        mockMvc.perform(get("/clientes/dni/12345678A"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Service Error"))
                .andExpect(jsonPath("$.path").value("/clientes/dni/12345678A"));
    }
}
