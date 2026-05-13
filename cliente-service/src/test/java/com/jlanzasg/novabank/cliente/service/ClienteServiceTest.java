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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private ClienteMapper clienteMapper;
    @InjectMocks
    private ClienteService clienteService;

    @Test
    void findById_WhenExists_ReturnsCliente() {
        Cliente cliente = Cliente.builder()
                .id(1L)
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan@test.com")
                .telefono("600000000")
                .fecha(LocalDateTime.now())
                .build();
        ClienteResponseDTO response = new ClienteResponseDTO();
        response.setId(1L);
        response.setDni("12345678A");

        when(clienteRepository.findById(1L)).thenReturn(Mono.just(cliente));
        when(clienteMapper.toResponseDTO(cliente)).thenReturn(response);

        StepVerifier.create(clienteService.findById(1L))
                .expectNextMatches(dto -> dto.getId().equals(1L) && dto.getDni().equals("12345678A"))
                .verifyComplete();
    }

    @Test
    void findById_WhenMissing_EmitsNotFound() {
        when(clienteRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(clienteService.findById(99L))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void save_WhenConflicts_EmitsDuplicateException() {
        ClienteRequestDTO request = new ClienteRequestDTO();
        request.setDni("12345678A");
        request.setNombre("Juan");
        request.setApellidos("Perez");
        request.setEmail("juan@test.com");
        request.setTelefono("600000000");

        when(clienteRepository.findConflictos("12345678A", "juan@test.com", "600000000"))
                .thenReturn(Flux.just("DNI"));

        StepVerifier.create(clienteService.save(request))
                .expectError(DuplicateException.class)
                .verify();

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deleteById_WhenExists_Completes() {
        Cliente cliente = Cliente.builder().id(3L).dni("12345678A").build();
        when(clienteRepository.findById(3L)).thenReturn(Mono.just(cliente));
        when(clienteRepository.delete(cliente)).thenReturn(Mono.empty());

        StepVerifier.create(clienteService.deleteById(3L))
                .verifyComplete();
    }
}
