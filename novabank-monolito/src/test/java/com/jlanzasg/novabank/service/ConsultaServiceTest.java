package com.jlanzasg.novabank.service;

import com.jlanzasg.novabank.dto.cuenta.response.CuentaSaldoResponseDTO;
import com.jlanzasg.novabank.dto.operacion.response.MovimientoResponseDTO;
import com.jlanzasg.novabank.exception.NotFoundException;
import com.jlanzasg.novabank.mapper.impl.CuentaMapper;
import com.jlanzasg.novabank.mapper.impl.OperacionMapper;
import com.jlanzasg.novabank.model.Cuenta;
import com.jlanzasg.novabank.model.Movimiento;
import com.jlanzasg.novabank.repository.ConsultasRepository;
import com.jlanzasg.novabank.repository.CuentaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaServiceTest {

    @Mock
    private ConsultasRepository consultasRepository;

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private OperacionMapper operacionMapper;

    @Mock
    private CuentaMapper cuentaMapper;

    @InjectMocks
    private ConsultaService consultaService;

    @Test
    void obtenerMovimientosPorCuentaYFecha_ConFechas_DevuelveMovimientos() {
        // GIVEN
        Long cuentaId = 1L;
        LocalDateTime fechaInicio = LocalDateTime.now().minusDays(7);
        LocalDateTime fechaFin = LocalDateTime.now();
        List<Movimiento> movimientos = List.of(new Movimiento(), new Movimiento());
        MovimientoResponseDTO responseDTO = new MovimientoResponseDTO();

        when(consultasRepository.findByCuentaIdAndFechaBetweenOrderByFechaDesc(cuentaId, fechaInicio, fechaFin))
                .thenReturn(movimientos);
        when(operacionMapper.toResponseDTO(any(Movimiento.class))).thenReturn(responseDTO);

        // WHEN
        List<MovimientoResponseDTO> resultados = consultaService.obtenerMovimientosPorCuentaYFecha(cuentaId, fechaInicio, fechaFin);

        // THEN
        assertNotNull(resultados);
        assertEquals(2, resultados.size());
        verify(consultasRepository, times(1)).findByCuentaIdAndFechaBetweenOrderByFechaDesc(cuentaId, fechaInicio, fechaFin);
        verify(operacionMapper, times(2)).toResponseDTO(any(Movimiento.class));
    }

    @Test
    void obtenerMovimientosPorCuentaYFecha_SinFechas_DevuelveMovimientos() {
        // GIVEN
        Long cuentaId = 1L;
        List<Movimiento> movimientos = List.of(new Movimiento(), new Movimiento());
        MovimientoResponseDTO responseDTO = new MovimientoResponseDTO();

        when(consultasRepository.findByCuentaIdOrderByFechaDesc(cuentaId)).thenReturn(movimientos);
        when(operacionMapper.toResponseDTO(any(Movimiento.class))).thenReturn(responseDTO);

        // WHEN
        List<MovimientoResponseDTO> resultados = consultaService.obtenerMovimientosPorCuentaYFecha(cuentaId, null, null);

        // THEN
        assertNotNull(resultados);
        assertEquals(2, resultados.size());
        verify(consultasRepository, times(1)).findByCuentaIdOrderByFechaDesc(cuentaId);
        verify(operacionMapper, times(2)).toResponseDTO(any(Movimiento.class));
    }

    @Test
    void obtenerMovimientosPorCuentaYFecha_SinMovimientos_DevuelveListaVacia() {
        // GIVEN
        Long cuentaId = 1L;
        when(consultasRepository.findByCuentaIdOrderByFechaDesc(cuentaId)).thenReturn(Collections.emptyList());

        // WHEN
        List<MovimientoResponseDTO> resultados = consultaService.obtenerMovimientosPorCuentaYFecha(cuentaId, null, null);

        // THEN
        assertNotNull(resultados);
        assertTrue(resultados.isEmpty());
        verify(consultasRepository, times(1)).findByCuentaIdOrderByFechaDesc(cuentaId);
        verify(operacionMapper, never()).toResponseDTO(any(Movimiento.class));
    }

    @Test
    void consultarSaldo_CuentaExiste_DevuelveSaldo() {
        // GIVEN
        String iban = "ES12345678901234567890";
        Cuenta cuenta = new Cuenta();
        cuenta.setBalance(1000.0);
        CuentaSaldoResponseDTO responseDTO = new CuentaSaldoResponseDTO();
        responseDTO.setBalance(1000.0);

        when(cuentaRepository.findByIban(iban)).thenReturn(Optional.of(cuenta));
        when(cuentaMapper.toOneValueResponseDTO(cuenta)).thenReturn(responseDTO);

        // WHEN
        CuentaSaldoResponseDTO resultado = consultaService.consultarSaldo(iban);

        // THEN
        assertNotNull(resultado);
        assertEquals(1000.0, resultado.getBalance());
        verify(cuentaRepository, times(1)).findByIban(iban);
        verify(cuentaMapper, times(1)).toOneValueResponseDTO(cuenta);
    }

    @Test
    void consultarSaldo_CuentaNoExiste_LanzaNotFoundException() {
        // GIVEN
        String iban = "ES00000000000000000000";
        when(cuentaRepository.findByIban(iban)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(NotFoundException.class, () -> {
            consultaService.consultarSaldo(iban);
        });
        verify(cuentaRepository, times(1)).findByIban(iban);
        verify(cuentaMapper, never()).toOneValueResponseDTO(any(Cuenta.class));
    }
}
