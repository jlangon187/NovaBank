package com.jlanzasg.novabank.cuenta.service;

import com.jlanzasg.novabank.cuenta.client.ClienteClient;
import com.jlanzasg.novabank.cuenta.dto.cliente.response.ClienteResponseDTO;
import com.jlanzasg.novabank.cuenta.dto.cuenta.response.CuentaResponseDTO;
import com.jlanzasg.novabank.cuenta.exception.NotFoundException;
import com.jlanzasg.novabank.cuenta.exception.ServiceException;
import com.jlanzasg.novabank.cuenta.mapper.impl.CuentaMapper;
import com.jlanzasg.novabank.cuenta.model.Cuenta;
import com.jlanzasg.novabank.cuenta.repository.CuentaRepository;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * The type Cuenta service test.
 */
@ExtendWith(MockitoExtension.class)
class CuentaServiceTest {

    @Mock
    private CuentaRepository cuentaRepository;
    @Mock
    private ClienteClient clienteClient;
    @Mock
    private CuentaMapper cuentaMapper;
    @InjectMocks
    private CuentaService cuentaService;

    /**
     * Crear cuenta when cliente exists returns response.
     */
    @Test
    void crearCuenta_WhenClienteExists_ReturnsResponse() {
        ClienteResponseDTO cliente = new ClienteResponseDTO();
        cliente.setId(1L);
        cliente.setNombre("Ana");
        cliente.setApellidos("Lopez");

        Cuenta cuenta = Cuenta.builder().id(1L).iban("ES91210000000000000001").clienteId(1L).balance(0.0).build();
        CuentaResponseDTO response = new CuentaResponseDTO();
        response.setIban("ES91210000000000000001");

        when(clienteClient.getClienteById(1L)).thenReturn(cliente);
        when(cuentaRepository.obtenerUltimoId()).thenReturn(Optional.of(0L));
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuenta);
        when(cuentaMapper.toResponseDTO(any(Cuenta.class), any(ClienteResponseDTO.class))).thenReturn(response);

        CuentaResponseDTO result = cuentaService.crearCuenta(1L, null);

        assertNotNull(result);
        assertEquals("ES91210000000000000001", result.getIban());
    }

    /**
     * Crear cuenta when cliente not found throws not found exception.
     */
    @Test
    void crearCuenta_WhenClienteNotFound_ThrowsNotFoundException() {
        when(clienteClient.getClienteById(9L)).thenThrow(mock(FeignException.NotFound.class));

        assertThrows(NotFoundException.class, () -> cuentaService.crearCuenta(9L, null));
    }

    /**
     * Find accounts by client id when cliente exists returns mapped accounts.
     */
    @Test
    void findAccountsByClientId_WhenClienteExists_ReturnsMappedAccounts() {
        ClienteResponseDTO cliente = new ClienteResponseDTO();
        cliente.setId(1L);

        when(clienteClient.getClienteById(1L)).thenReturn(cliente);
        when(cuentaRepository.findAllByClienteId(1L)).thenReturn(Set.of());
        when(cuentaMapper.toSimpleResponseDTOList(Set.of())).thenReturn(Set.of());

        assertEquals(0, cuentaService.findAccountsByClientId(1L).size());
    }

    /**
     * Actualizar saldo when account exists saves updated balance.
     */
    @Test
    void actualizarSaldo_WhenAccountExists_SavesUpdatedBalance() {
        Cuenta cuenta = Cuenta.builder().iban("ES91210000000000000001").balance(100.0).clienteId(1L).build();
        when(cuentaRepository.findByIban("ES91210000000000000001")).thenReturn(Optional.of(cuenta));

        cuentaService.actualizarSaldo("ES91210000000000000001", 250.0);

        assertEquals(250.0, cuenta.getBalance());
        verify(cuentaRepository).save(cuenta);
    }

    /**
     * Generar iban when repository empty starts from one.
     */
    @Test
    void generarIban_WhenRepositoryEmpty_StartsFromOne() {
        when(cuentaRepository.obtenerUltimoId()).thenReturn(Optional.empty());

        String iban = cuentaService.generarIban();

        assertTrue(iban.startsWith("ES91210000"));
        assertTrue(iban.endsWith("000000000001"));
    }

    /**
     * Find account by iban when cliente service fails throws service exception.
     */
    @Test
    void findAccountByIban_WhenClienteServiceFails_ThrowsServiceException() {
        Cuenta cuenta = Cuenta.builder().id(1L).iban("ES91210000000000000001").clienteId(7L).balance(50.0).build();

        when(cuentaRepository.findByIban("ES91210000000000000001")).thenReturn(Optional.of(cuenta));
        when(clienteClient.getClienteById(7L)).thenThrow(mock(FeignException.InternalServerError.class));

        assertThrows(ServiceException.class, () -> cuentaService.findAccountByIban("ES91210000000000000001"));
    }

    /**
     * Actualizar saldo when account missing throws not found exception.
     */
    @Test
    void actualizarSaldo_WhenAccountMissing_ThrowsNotFoundException() {
        when(cuentaRepository.findByIban("ES404")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> cuentaService.actualizarSaldo("ES404", 99.0));
    }
}
