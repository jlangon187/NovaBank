package com.jlanzasg.novabank.cliente.service;

import com.jlanzasg.novabank.cliente.dto.cliente.request.ClienteRequestDTO;
import com.jlanzasg.novabank.cliente.dto.cliente.response.ClienteResponseDTO;
import com.jlanzasg.novabank.cliente.exception.DuplicateException;
import com.jlanzasg.novabank.cliente.exception.NotFoundException;
import com.jlanzasg.novabank.cliente.mapper.impl.ClienteMapper;
import com.jlanzasg.novabank.cliente.model.Cliente;
import com.jlanzasg.novabank.cliente.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * The type Cliente service test.
 */
@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private ClienteMapper clienteMapper;
    @InjectMocks
    private ClienteService clienteService;

    /**
     * Save when dni exists throws duplicate exception.
     */
    @Test
    void save_WhenDniExists_ThrowsDuplicateException() {
        ClienteRequestDTO request = new ClienteRequestDTO();
        request.setDni("12345678A");
        request.setEmail("a@a.com");
        request.setTelefono("600000000");

        when(clienteRepository.findConflictos(anyString(), anyString(), anyString())).thenReturn(List.of("DNI"));

        assertThrows(DuplicateException.class, () -> clienteService.save(request));
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    /**
     * Save when no conflicts saves and returns mapped response.
     */
    @Test
    void save_WhenNoConflicts_SavesAndReturnsMappedResponse() {
        ClienteRequestDTO request = new ClienteRequestDTO();
        request.setDni("12345678A");
        request.setEmail("a@a.com");
        request.setTelefono("600000000");

        Cliente entity = Cliente.builder().dni("12345678A").email("a@a.com").telefono("600000000").build();
        ClienteResponseDTO response = new ClienteResponseDTO();
        response.setDni("12345678A");

        when(clienteRepository.findConflictos(anyString(), anyString(), anyString())).thenReturn(List.of());
        when(clienteMapper.toEntity(request)).thenReturn(entity);
        when(clienteMapper.toResponseDTO(entity)).thenReturn(response);

        ClienteResponseDTO result = clienteService.save(request);

        assertNotNull(result);
        assertEquals("12345678A", result.getDni());
        verify(clienteRepository).save(entity);
    }

    /**
     * Find by id when not exists throws not found exception.
     */
    @Test
    void findById_WhenNotExists_ThrowsNotFoundException() {
        when(clienteRepository.findById(44L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> clienteService.findById(44L));
    }

    /**
     * Delete by id when exists deletes entity.
     */
    @Test
    void deleteById_WhenExists_DeletesEntity() {
        when(clienteRepository.existsById(3L)).thenReturn(true);

        clienteService.deleteById(3L);

        verify(clienteRepository).deleteById(3L);
    }

    /**
     * Delete by id when not exists throws not found exception.
     */
    @Test
    void deleteById_WhenNotExists_ThrowsNotFoundException() {
        when(clienteRepository.existsById(77L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> clienteService.deleteById(77L));
        verify(clienteRepository, never()).deleteById(anyLong());
    }

    /**
     * Find by dni when exists returns mapped response.
     */
    @Test
    void findByDni_WhenExists_ReturnsMappedResponse() {
        Cliente entity = Cliente.builder().dni("12345678A").nombre("Ana").apellidos("Lopez").email("ana@test.com").telefono("600111222").build();
        ClienteResponseDTO response = new ClienteResponseDTO();
        response.setDni("12345678A");

        when(clienteRepository.findByDni("12345678A")).thenReturn(Optional.of(entity));
        when(clienteMapper.toResponseDTO(entity)).thenReturn(response);

        ClienteResponseDTO result = clienteService.findByDni("12345678a");

        assertEquals("12345678A", result.getDni());
    }
}
