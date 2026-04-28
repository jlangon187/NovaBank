package com.jlanzasg.novabank.service;

import com.jlanzasg.novabank.dto.operacion.request.OperacionRequestDTO;
import com.jlanzasg.novabank.dto.operacion.request.TransferenciaRequestDTO;
import com.jlanzasg.novabank.dto.operacion.response.MovimientoResponseDTO;
import com.jlanzasg.novabank.exception.DuplicateException;
import com.jlanzasg.novabank.exception.NotFoundException;
import com.jlanzasg.novabank.exception.SaldoInsuficienteException;
import com.jlanzasg.novabank.mapper.impl.OperacionMapper;
import com.jlanzasg.novabank.model.Cuenta;
import com.jlanzasg.novabank.model.Movimiento;
import com.jlanzasg.novabank.model.TipoMovimiento;
import com.jlanzasg.novabank.repository.CuentaRepository;
import com.jlanzasg.novabank.repository.OperacionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OperacionServiceTest {

    @Mock
    private OperacionRepository operacionRepository;

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private OperacionMapper operacionMapper;

    @Mock
    private CuentaService cuentaService;

    @InjectMocks
    private OperacionService operacionService;

    @Test
    void depositar_Exito() {
        // GIVEN
        OperacionRequestDTO dto = new OperacionRequestDTO();
        dto.setIbanCuenta("ES123");
        dto.setImporte(100.0);

        Cuenta cuenta = Cuenta.builder()
                .iban("ES123")
                .balance(500.0)
                .build();

        Movimiento movimiento = new Movimiento();
        Movimiento movimientoGuardado = new Movimiento();
        MovimientoResponseDTO responseDTO = new MovimientoResponseDTO();

        when(cuentaRepository.findByIban("ES123")).thenReturn(Optional.of(cuenta));
        when(operacionMapper.toEntity(dto)).thenReturn(movimiento);
        when(operacionRepository.save(any(Movimiento.class))).thenReturn(movimientoGuardado);
        when(operacionMapper.toResponseDTO(movimientoGuardado)).thenReturn(responseDTO);

        // WHEN
        MovimientoResponseDTO result = operacionService.depositar(dto);

        // THEN
        assertNotNull(result);
        assertEquals(600.0, cuenta.getBalance());
        verify(cuentaRepository).save(cuenta);
        verify(operacionRepository).save(movimiento);
    }

    @Test
    void depositar_ImporteInvalido_LanzaExcepcion() {
        OperacionRequestDTO dto = new OperacionRequestDTO();
        dto.setImporte(0.0);

        assertThrows(IllegalArgumentException.class, () -> operacionService.depositar(dto));
    }

    @Test
    void depositar_CuentaNoExiste_LanzaExcepcion() {
        OperacionRequestDTO dto = new OperacionRequestDTO();
        dto.setIbanCuenta("ES999");
        dto.setImporte(100.0);

        when(cuentaRepository.findByIban("ES999")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> operacionService.depositar(dto));
    }

    @Test
    void retirar_Exito() {
        // GIVEN
        OperacionRequestDTO dto = new OperacionRequestDTO();
        dto.setIbanCuenta("ES123");
        dto.setImporte(100.0);

        Cuenta cuenta = Cuenta.builder()
                .iban("ES123")
                .balance(500.0)
                .build();

        Movimiento movimiento = new Movimiento();
        Movimiento movimientoGuardado = new Movimiento();
        MovimientoResponseDTO responseDTO = new MovimientoResponseDTO();

        when(cuentaRepository.findByIban("ES123")).thenReturn(Optional.of(cuenta));
        when(operacionMapper.toEntity(dto)).thenReturn(movimiento);
        when(operacionRepository.save(any(Movimiento.class))).thenReturn(movimientoGuardado);
        when(operacionMapper.toResponseDTO(movimientoGuardado)).thenReturn(responseDTO);

        // WHEN
        MovimientoResponseDTO result = operacionService.retirar(dto);

        // THEN
        assertNotNull(result);
        assertEquals(400.0, cuenta.getBalance());
        verify(cuentaRepository).save(cuenta);
    }

    @Test
    void retirar_SaldoInsuficiente_LanzaExcepcion() {
        OperacionRequestDTO dto = new OperacionRequestDTO();
        dto.setIbanCuenta("ES123");
        dto.setImporte(1000.0);

        Cuenta cuenta = new Cuenta();
        cuenta.setBalance(500.0);

        when(cuentaRepository.findByIban("ES123")).thenReturn(Optional.of(cuenta));

        assertThrows(SaldoInsuficienteException.class, () -> operacionService.retirar(dto));
    }

    @Test
    void transferir_Exito() {
        // GIVEN
        TransferenciaRequestDTO dto = new TransferenciaRequestDTO();
        dto.setCuentaOrigen("ES_ORG");
        dto.setCuentaDestino("ES_DST");
        dto.setImporte(100.0);

        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setBalance(500.0);
        Cuenta cuentaDestino = new Cuenta();
        cuentaDestino.setBalance(200.0);

        when(cuentaRepository.findByIban("ES_ORG")).thenReturn(Optional.of(cuentaOrigen));
        when(cuentaRepository.findByIban("ES_DST")).thenReturn(Optional.of(cuentaDestino));
        when(operacionRepository.save(any(Movimiento.class))).thenReturn(new Movimiento());
        when(operacionMapper.toResponseDTO(any())).thenReturn(new MovimientoResponseDTO());

        // WHEN
        List<MovimientoResponseDTO> result = operacionService.transferir(dto);

        // THEN
        assertEquals(2, result.size());
        assertEquals(400.0, cuentaOrigen.getBalance());
        assertEquals(300.0, cuentaDestino.getBalance());
        verify(cuentaRepository, times(2)).save(any(Cuenta.class));
        verify(operacionRepository, times(2)).save(any(Movimiento.class));
    }

    @Test
    void transferir_MismaCuenta_LanzaExcepcion() {
        TransferenciaRequestDTO dto = new TransferenciaRequestDTO();
        dto.setCuentaOrigen("ES123");
        dto.setCuentaDestino("ES123");
        dto.setImporte(100.0);

        assertThrows(DuplicateException.class, () -> operacionService.transferir(dto));
    }
}
