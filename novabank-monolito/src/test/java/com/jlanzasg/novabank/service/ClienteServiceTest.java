package com.jlanzasg.novabank.service;

import com.jlanzasg.novabank.dto.cliente.request.ClienteRequestDTO;
import com.jlanzasg.novabank.dto.cliente.response.ClienteResponseDTO;
import com.jlanzasg.novabank.exception.DuplicateException;
import com.jlanzasg.novabank.exception.NotFoundException;
import com.jlanzasg.novabank.mapper.impl.ClienteMapper;
import com.jlanzasg.novabank.model.Cliente;
import com.jlanzasg.novabank.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void save_ClienteExito() {
        // 1. GIVEN (Preparación)
        ClienteRequestDTO request = new ClienteRequestDTO();
        request.setDni("12345678A");
        request.setEmail("test@test.com");
        request.setTelefono("600000000");

        Cliente clienteMapeado = new Cliente();
        ClienteResponseDTO responseEsperada = new ClienteResponseDTO();
        responseEsperada.setId(1L);

        // Simulamos TU método optimizado 'findConflictos' para que devuelva una lista vacía (no hay conflictos)
        when(clienteRepository.findConflictos(anyString(), anyString(), anyString())).thenReturn(Collections.emptyList());

        when(clienteMapper.toEntity(any(ClienteRequestDTO.class))).thenReturn(clienteMapeado);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteMapeado);
        when(clienteMapper.toResponseDTO(any(Cliente.class))).thenReturn(responseEsperada);

        // 2. WHEN (Ejecución)
        ClienteResponseDTO resultado = clienteService.save(request);

        // 3. THEN (Verificación)
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void save_DniDuplicado_LanzaDuplicateException() {
        // 1. GIVEN
        ClienteRequestDTO request = new ClienteRequestDTO();
        request.setDni("12345678A");
        request.setEmail("test@test.com");
        request.setTelefono("600000000");

        // Simulamos que la BD encuentra un conflicto con el DNI
        when(clienteRepository.findConflictos(anyString(), anyString(), anyString())).thenReturn(List.of("DNI"));

        // 2 & 3. WHEN & THEN
        DuplicateException exception = assertThrows(DuplicateException.class, () -> {
            clienteService.save(request);
        });

        assertTrue(exception.getMessage().toUpperCase().contains("DNI"));
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void save_EmailDuplicado_LanzaDuplicateException() {
        // 1. GIVEN
        ClienteRequestDTO request = new ClienteRequestDTO();
        request.setDni("12345678A");
        request.setEmail("test@test.com");
        request.setTelefono("600000000");

        // Simulamos que la BD encuentra un conflicto con el EMAIL
        when(clienteRepository.findConflictos(anyString(), anyString(), anyString())).thenReturn(List.of("EMAIL"));

        // 2 & 3. WHEN & THEN
        DuplicateException exception = assertThrows(DuplicateException.class, () -> {
            clienteService.save(request);
        });

        assertTrue(exception.getMessage().toUpperCase().contains("EMAIL"));
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void save_TelefonoDuplicado_LanzaDuplicateException() {
        // 1. GIVEN
        ClienteRequestDTO request = new ClienteRequestDTO();
        request.setDni("12345678A");
        request.setEmail("test@test.com");
        request.setTelefono("600000000");

        // Simulamos que la BD encuentra un conflicto con el TELEFONO
        when(clienteRepository.findConflictos(anyString(), anyString(), anyString())).thenReturn(List.of("TELEFONO"));

        // 2 & 3. WHEN & THEN
        DuplicateException exception = assertThrows(DuplicateException.class, () -> {
            clienteService.save(request);
        });

        assertTrue(exception.getMessage().contains("teléfono"));
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void findById_Exito() {
        // 1. GIVEN
        Long id = 1L;
        Cliente cliente = new Cliente();
        cliente.setId(id);

        ClienteResponseDTO response = new ClienteResponseDTO();
        response.setId(id);

        when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));
        when(clienteMapper.toResponseDTO(cliente)).thenReturn(response);

        // 2. WHEN
        ClienteResponseDTO resultado = clienteService.findById(id);

        // 3. THEN
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
    }

    @Test
    void findById_NoEncontrado_LanzaNotFoundException() {
        // 1. GIVEN
        Long id = 99L;
        when(clienteRepository.findById(id)).thenReturn(Optional.empty());

        // 2 & 3. WHEN & THEN
        assertThrows(NotFoundException.class, () -> {
            clienteService.findById(id);
        });
    }

    @Test
    void findByDni_Exito() {
        // 1. GIVEN
        String dni = "12345678A";
        Cliente cliente = new Cliente();
        cliente.setDni(dni);
        ClienteResponseDTO response = new ClienteResponseDTO();
        response.setDni(dni);

        when(clienteRepository.findByDni(dni)).thenReturn(Optional.of(cliente));
        when(clienteMapper.toResponseDTO(cliente)).thenReturn(response);

        // 2. WHEN
        ClienteResponseDTO resultado = clienteService.findByDni(dni);

        // 3. THEN
        assertNotNull(resultado);
        assertEquals(dni, resultado.getDni());
    }

    @Test
    void findByDni_NoEncontrado_LanzaNotFoundException() {
        // 1. GIVEN
        String dni = "11111111Z";
        when(clienteRepository.findByDni(dni)).thenReturn(Optional.empty());

        // 2 & 3. WHEN & THEN
        assertThrows(NotFoundException.class, () -> {
            clienteService.findByDni(dni);
        });
    }

    @Test
    void findAll_ClientesExistentes_DevuelveLista() {
        // 1. GIVEN
        List<Cliente> clientes = List.of(new Cliente(), new Cliente());
        when(clienteRepository.findAll()).thenReturn(clientes);
        when(clienteMapper.toResponseDTO(any(Cliente.class))).thenReturn(new ClienteResponseDTO());

        // 2. WHEN
        List<ClienteResponseDTO> resultados = clienteService.findAll();

        // 3. THEN
        assertNotNull(resultados);
        assertEquals(2, resultados.size());
        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    void findAll_SinClientes_DevuelveListaVacia() {
        // 1. GIVEN
        when(clienteRepository.findAll()).thenReturn(Collections.emptyList());

        // 2. WHEN
        List<ClienteResponseDTO> resultados = clienteService.findAll();

        // 3. THEN
        assertNotNull(resultados);
        assertTrue(resultados.isEmpty());
        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    void deleteById_Exito() {
        // 1. GIVEN
        Long id = 1L;
        when(clienteRepository.existsById(id)).thenReturn(true);

        // 2. WHEN
        assertDoesNotThrow(() -> clienteService.deleteById(id));

        // 3. THEN
        verify(clienteRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteById_NoEncontrado_LanzaNotFoundException() {
        // 1. GIVEN
        Long id = 99L;
        when(clienteRepository.existsById(id)).thenReturn(false);

        // 2 & 3. WHEN & THEN
        assertThrows(NotFoundException.class, () -> {
            clienteService.deleteById(id);
        });
        verify(clienteRepository, never()).deleteById(anyLong()); // Aseguramos que no se intenta eliminar
    }
}
