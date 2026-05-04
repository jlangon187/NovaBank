package com.jlanzasg.novabank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlanzasg.novabank.dto.cliente.request.ClienteRequestDTO;
import com.jlanzasg.novabank.dto.cliente.response.ClienteResponseDTO;
import com.jlanzasg.novabank.exception.DuplicateException;
import com.jlanzasg.novabank.exception.NotFoundException;
import com.jlanzasg.novabank.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClienteService clienteService;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void saveCliente_Exito_Devuelve201() throws Exception {
        // GIVEN
        ClienteRequestDTO request = new ClienteRequestDTO();
        request.setDni("12345678A");
        request.setEmail("test@example.com");
        request.setTelefono("600111222");
        request.setNombre("Test Name");
        request.setApellidos("Test Lastname");

        ClienteResponseDTO response = new ClienteResponseDTO();
        response.setId(1L);
        response.setDni("12345678A");

        when(clienteService.save(any(ClienteRequestDTO.class))).thenReturn(response);

        // WHEN & THEN
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.dni").value("12345678A"));

        verify(clienteService, times(1)).save(any(ClienteRequestDTO.class));
    }

    @Test
    void saveCliente_ValidacionFalla_Devuelve400() throws Exception {
        // GIVEN: DNI is null, which is a validation error
        ClienteRequestDTO request = new ClienteRequestDTO();
        request.setEmail("test@example.com");
        request.setTelefono("600111222");

        // WHEN & THEN
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(clienteService, never()).save(any(ClienteRequestDTO.class));
    }

    @Test
    void saveCliente_DniDuplicado_Devuelve409() throws Exception {
        // GIVEN
        ClienteRequestDTO request = new ClienteRequestDTO();
        request.setDni("12345678A");
        request.setEmail("test@example.com");
        request.setTelefono("600111222");
        request.setNombre("Test Name");
        request.setApellidos("Test Lastname");

        when(clienteService.save(any(ClienteRequestDTO.class)))
                .thenThrow(new DuplicateException("El DNI 12345678A ya existe en la base de datos"));

        // WHEN & THEN
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("El DNI 12345678A ya existe en la base de datos"));

        verify(clienteService, times(1)).save(any(ClienteRequestDTO.class));
    }

    @Test
    void saveCliente_EmailDuplicado_Devuelve409() throws Exception {
        // GIVEN
        ClienteRequestDTO request = new ClienteRequestDTO();
        request.setDni("12345678A");
        request.setEmail("test@example.com");
        request.setTelefono("600111222");
        request.setNombre("Test Name");
        request.setApellidos("Test Lastname");

        when(clienteService.save(any(ClienteRequestDTO.class)))
                .thenThrow(new DuplicateException("El email test@example.com ya existe en la base de datos"));

        // WHEN & THEN
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("El email test@example.com ya existe en la base de datos"));

        verify(clienteService, times(1)).save(any(ClienteRequestDTO.class));
    }

    @Test
    void saveCliente_TelefonoDuplicado_Devuelve409() throws Exception {
        // GIVEN
        ClienteRequestDTO request = new ClienteRequestDTO();
        request.setDni("12345678A");
        request.setEmail("test@example.com");
        request.setTelefono("600111222");
        request.setNombre("Test Name");
        request.setApellidos("Test Lastname");

        when(clienteService.save(any(ClienteRequestDTO.class)))
                .thenThrow(new DuplicateException("El teléfono 600111222 ya existe en la base de datos"));

        // WHEN & THEN
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("El teléfono 600111222 ya existe en la base de datos"));

        verify(clienteService, times(1)).save(any(ClienteRequestDTO.class));
    }

    @Test
    void findById_Exito_Devuelve200() throws Exception {
        // GIVEN
        Long id = 1L;
        ClienteResponseDTO response = new ClienteResponseDTO();
        response.setId(id);
        response.setDni("12345678A");

        when(clienteService.findById(id)).thenReturn(response);

        // WHEN & THEN
        mockMvc.perform(get("/clientes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.dni").value("12345678A"));

        verify(clienteService, times(1)).findById(id);
    }

    @Test
    void findById_NoEncontrado_Devuelve404() throws Exception {
        // GIVEN
        Long id = 99L;
        when(clienteService.findById(id)).thenThrow(new NotFoundException("No se ha encontrado el cliente con el id: " + id));

        // WHEN & THEN
        mockMvc.perform(get("/clientes/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No se ha encontrado el cliente con el id: 99"));

        verify(clienteService, times(1)).findById(id);
    }

    @Test
    void findByDni_Exito_Devuelve200() throws Exception {
        // GIVEN
        String dni = "12345678A";
        ClienteResponseDTO response = new ClienteResponseDTO();
        response.setDni(dni);

        when(clienteService.findByDni(dni)).thenReturn(response);

        // WHEN & THEN
        mockMvc.perform(get("/clientes/dni/{dni}", dni))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dni").value(dni));

        verify(clienteService, times(1)).findByDni(dni);
    }

    @Test
    void findByDni_NoEncontrado_Devuelve404() throws Exception {
        // GIVEN
        String dni = "99999999X";
        when(clienteService.findByDni(dni)).thenThrow(new NotFoundException("Cliente con DNI " + dni + " no encontrado"));

        // WHEN & THEN
        mockMvc.perform(get("/clientes/dni/{dni}", dni))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cliente con DNI 99999999X no encontrado"));

        verify(clienteService, times(1)).findByDni(dni);
    }

    @Test
    void listarClientes_Exito_Devuelve200() throws Exception {
        // GIVEN
        List<ClienteResponseDTO> clientes = List.of(new ClienteResponseDTO(), new ClienteResponseDTO());
        when(clienteService.findAll()).thenReturn(clientes);

        // WHEN & THEN
        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));

        verify(clienteService, times(1)).findAll();
    }

    @Test
    void listarClientes_Vacio_Devuelve200YListaVacia() throws Exception {
        // GIVEN
        when(clienteService.findAll()).thenReturn(Collections.emptyList());

        // WHEN & THEN
        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));

        verify(clienteService, times(1)).findAll();
    }

    @Test
    void deleteCliente_Exito_Devuelve204() throws Exception {
        // GIVEN
        Long id = 1L;
        doNothing().when(clienteService).deleteById(id);

        // WHEN & THEN
        mockMvc.perform(delete("/clientes/{id}", id))
                .andExpect(status().isNoContent());

        verify(clienteService, times(1)).deleteById(id);
    }

    @Test
    void deleteCliente_NoEncontrado_Devuelve404() throws Exception {
        // GIVEN
        Long id = 99L;
        doThrow(new NotFoundException("Cliente con id " + id + " no encontrado"))
                .when(clienteService).deleteById(id);

        // WHEN & THEN
        mockMvc.perform(delete("/clientes/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cliente con id 99 no encontrado"));

        verify(clienteService, times(1)).deleteById(id);
    }

    // TODO: Add tests for authenticated endpoints if security is implemented and configured in ClienteController
}
