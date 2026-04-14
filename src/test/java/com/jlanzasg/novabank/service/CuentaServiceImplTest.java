package com.jlanzasg.novabank.service;

import com.jlanzasg.novabank.model.Cliente;
import com.jlanzasg.novabank.model.Cuenta;
import com.jlanzasg.novabank.repository.CuentaRepository;
import com.jlanzasg.novabank.service.Impl.CuentaServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CuentaServiceImplTest {

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private CuentaServiceImpl cuentaService;

    @Test
    void crearCuenta_clienteExiste_generaIbanYGuarda() {
        Long clienteId = 1L;
        Cliente cliente = Cliente.builder().id(clienteId).build();

        when(clienteService.buscarClientePorId(clienteId)).thenReturn(Optional.of(cliente));
        when(cuentaRepository.obtenerUltimoId()).thenReturn(5L);
        when(cuentaRepository.guardar(any(Cuenta.class))).thenAnswer(i -> i.getArguments()[0]);

        Cuenta cuentaCreada = cuentaService.crearCuenta(clienteId);

        assertNotNull(cuentaCreada);
        assertEquals("ES91210000000000000006", cuentaCreada.getIban());
        verify(cuentaRepository, times(1)).guardar(any(Cuenta.class));
    }

    @Test
    void crearCuenta_clienteNoExiste_lanzaExcepcion() {
        Long clienteId = 99L;
        when(clienteService.buscarClientePorId(clienteId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> cuentaService.crearCuenta(clienteId));
        verify(cuentaRepository, never()).guardar(any());
    }
}