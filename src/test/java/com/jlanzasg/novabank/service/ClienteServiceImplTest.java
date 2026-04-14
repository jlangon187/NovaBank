package com.jlanzasg.novabank.service;

import com.jlanzasg.novabank.model.Cliente;
import com.jlanzasg.novabank.repository.ClienteRepository;
import com.jlanzasg.novabank.service.Impl.ClienteServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceImplTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ClienteServiceImpl service;

    @Test
    void registrarCliente_conDatosValidos_guardaCorrectamente() {
        Cliente cliente = Cliente.builder()
                .nombre("Juan")
                .apellido("Perez")
                .dni("12345678A")
                .email("juan@test.com")
                .telefono("600123456")
                .build();

        when(repository.buscarPorDni("12345678A")).thenReturn(Optional.empty());

        service.registrarCliente(cliente);

        verify(repository, times(1)).guardar(cliente);
    }

    @Test
    void registrarCliente_conDniInvalido_lanzaExcepcion() {
        Cliente cliente = Cliente.builder()
                .nombre("Juan")
                .apellido("Perez")
                .dni("DNI_MAL")
                .email("juan@test.com")
                .telefono("600123456")
                .build();

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            service.registrarCliente(cliente);
        });

        assertTrue(e.getMessage().contains("formato del DNI"));
        verify(repository, never()).guardar(any(Cliente.class));
    }
}