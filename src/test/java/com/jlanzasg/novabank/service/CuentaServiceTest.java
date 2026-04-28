package com.jlanzasg.novabank.service;

import com.jlanzasg.novabank.dto.cuenta.response.CuentaResponseDTO;
import com.jlanzasg.novabank.dto.cuenta.response.CuentaSimpleResponseDTO;
import com.jlanzasg.novabank.exception.NotFoundException;
import com.jlanzasg.novabank.mapper.impl.CuentaMapper;
import com.jlanzasg.novabank.model.Cliente;
import com.jlanzasg.novabank.model.Cuenta;
import com.jlanzasg.novabank.repository.ClienteRepository;
import com.jlanzasg.novabank.repository.CuentaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CuentaServiceTest {

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private CuentaMapper cuentaMapper;

    @InjectMocks
    private CuentaService cuentaService;

    @Test
    void crearCuenta_ClienteExiste_DevuelveCuentaResponseDTO() {
        // GIVEN
        Long clienteId = 1L;
        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        cliente.setCuentas(new HashSet<>());

        Cuenta cuentaGenerada = new Cuenta();
        cuentaGenerada.setId(1L);
        // El objeto Cuenta no se modifica con setIban aquí, asumimos que crearCuenta se encarga de esto internamente

        CuentaResponseDTO responseDTO = new CuentaResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setIban("ES91210000000000000001");

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(cuentaRepository.obtenerUltimoId()).thenReturn(Optional.of(0L)); 
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuentaGenerada);
        when(cuentaMapper.toResponseDTO(any(Cuenta.class))).thenReturn(responseDTO);

        // WHEN
        CuentaResponseDTO resultado = cuentaService.crearCuenta(clienteId);

        // THEN
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("ES91210000000000000001", resultado.getIban());
        verify(clienteRepository, times(1)).findById(clienteId);
        verify(cuentaRepository, times(1)).obtenerUltimoId();
        verify(cuentaRepository, times(1)).save(any(Cuenta.class));
        verify(cuentaMapper, times(1)).toResponseDTO(any(Cuenta.class));
        assertFalse(cliente.getCuentas().isEmpty());
    }

    @Test
    void crearCuenta_ClienteNoExiste_LanzaNotFoundException() {
        // GIVEN
        Long clienteId = 99L;
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(NotFoundException.class, () -> {
            cuentaService.crearCuenta(clienteId);
        });
        verify(clienteRepository, times(1)).findById(clienteId);
        verify(cuentaRepository, never()).obtenerUltimoId();
        verify(cuentaRepository, never()).save(any(Cuenta.class));
        verify(cuentaMapper, never()).toResponseDTO(any(Cuenta.class));
    }

    @Test
    void findAccountsByClientId_ClienteExiste_DevuelveCuentasSimples() {
        // GIVEN
        Long clienteId = 1L;
        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        // IMPORTANTE: Cuenta tiene equals/hashCode basado en iban (y aquí es null), así que Set.of(...) detecta duplicados.
        // Para que el Set sea válido, asignamos IBANs distintos usando el builder (no hay setter de iban).
        Cuenta cuenta1 = Cuenta.builder().iban("ES91210000000000000001").build();
        cuenta1.setId(1L);
        Cuenta cuenta2 = Cuenta.builder().iban("ES91210000000000000002").build();
        cuenta2.setId(2L);
        cliente.setCuentas(new HashSet<>(Set.of(cuenta1, cuenta2)));

        // Igual que con Cuenta, CuentaSimpleResponseDTO puede tener equals/hashCode por campos (iban/balance),
        // y si los dejamos a null se consideran duplicados al construir Set.of(...).
        CuentaSimpleResponseDTO simpleResponse1 = new CuentaSimpleResponseDTO();
        simpleResponse1.setIban("ES91210000000000000001");
        CuentaSimpleResponseDTO simpleResponse2 = new CuentaSimpleResponseDTO();
        simpleResponse2.setIban("ES91210000000000000002");

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(cuentaMapper.toSimpleResponseDTOList(anySet()))
                .thenReturn(new HashSet<>(Set.of(simpleResponse1, simpleResponse2)));

        // WHEN
        Set<CuentaSimpleResponseDTO> resultados = cuentaService.findAccountsByClientId(clienteId);

        // THEN
        assertNotNull(resultados);
        assertEquals(2, resultados.size());
        verify(clienteRepository, times(1)).findById(clienteId);
        verify(cuentaMapper, times(1)).toSimpleResponseDTOList(cliente.getCuentas());
    }

    @Test
    void findAccountsByClientId_ClienteNoExiste_LanzaNotFoundException() {
        // GIVEN
        Long clienteId = 99L;
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(NotFoundException.class, () -> {
            cuentaService.findAccountsByClientId(clienteId);
        });
        verify(clienteRepository, times(1)).findById(clienteId);
        verify(cuentaMapper, never()).toSimpleResponseDTOList(anySet());
    }

    @Test
    void findAccountByIban_CuentaExiste_DevuelveCuentaResponseDTO() {
        // GIVEN
        String iban = "ES12345678901234567890";
        Cuenta cuenta = new Cuenta();
        // Removed setIban call to respect constraints
        CuentaResponseDTO responseDTO = new CuentaResponseDTO();
        responseDTO.setIban(iban);

        when(cuentaRepository.findByIban(iban)).thenReturn(Optional.of(cuenta));
        when(cuentaMapper.toResponseDTO(cuenta)).thenReturn(responseDTO);

        // WHEN
        CuentaResponseDTO resultado = cuentaService.findAccountByIban(iban);

        // THEN
        assertNotNull(resultado);
        assertEquals(iban, resultado.getIban());
        verify(cuentaRepository, times(1)).findByIban(iban);
        verify(cuentaMapper, times(1)).toResponseDTO(cuenta);
    }

    @Test
    void findAccountByIban_CuentaNoExiste_LanzaNotFoundException() {
        // GIVEN
        String iban = "ES00000000000000000000";
        when(cuentaRepository.findByIban(iban)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(NotFoundException.class, () -> {
            cuentaService.findAccountByIban(iban);
        });
        verify(cuentaRepository, times(1)).findByIban(iban);
        verify(cuentaMapper, never()).toResponseDTO(any(Cuenta.class));
    }

    @Test
    void generarIban_PrimerIban_DevuelveIbanCorrecto() {
        // GIVEN
        when(cuentaRepository.obtenerUltimoId()).thenReturn(Optional.of(0L));

        // WHEN
        String iban = cuentaService.generarIban();

        // THEN
        assertNotNull(iban);
        assertEquals("ES91210000000000000001", iban);
        verify(cuentaRepository, times(1)).obtenerUltimoId();
    }

    @Test
    void generarIban_SiguienteIban_DevuelveIbanCorrecto() {
        // GIVEN
        when(cuentaRepository.obtenerUltimoId()).thenReturn(Optional.of(5L));

        // WHEN
        String iban = cuentaService.generarIban();

        // THEN
        assertNotNull(iban);
        assertEquals("ES91210000000000000006", iban);
        verify(cuentaRepository, times(1)).obtenerUltimoId();
    }
}
