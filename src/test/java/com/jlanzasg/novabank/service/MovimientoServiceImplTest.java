package com.jlanzasg.novabank.service;

import com.jlanzasg.novabank.repository.MovimientoRepository;
import com.jlanzasg.novabank.service.Impl.MovimientoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimientoServiceImplTest {

    @Mock
    private MovimientoRepository movimientoRepository;

    @Mock
    private CuentaRepository cuentaRepository;

    @InjectMocks
    private MovimientoServiceImpl movimientoService;

    @Test
    void consultarHistorial_cuentaExiste_devuelveMovimientos() {
        String iban = "ES123456789";
        Cuenta cuenta = Cuenta.builder().id(1L).iban(iban).build();
        List<Movimiento> movimientosSimulados = List.of(
                Movimiento.builder().id(10L).cantidad(50.0).build(),
                Movimiento.builder().id(11L).cantidad(20.0).build()
        );

        when(cuentaRepository.buscarPorNumero(iban)).thenReturn(Optional.of(cuenta));
        when(movimientoRepository.buscarPorCuentaId(1L)).thenReturn(movimientosSimulados);

        List<Movimiento> resultado = movimientoService.consultarHistorial(iban);

        assertEquals(2, resultado.size());
        verify(cuentaRepository, times(1)).buscarPorNumero(iban);
        verify(movimientoRepository, times(1)).buscarPorCuentaId(1L);
    }

    @Test
    void consultarHistorialPorFechas_fechasInvertidas_lanzaExcepcion() {
        String iban = "ES123456789";
        LocalDateTime inicio = LocalDateTime.of(2023, 12, 31, 0, 0);
        LocalDateTime fin = LocalDateTime.of(2023, 1, 1, 0, 0); // Fecha fin es ANTERIOR a inicio

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            movimientoService.consultarHistorialPorFechas(iban, inicio, fin);
        });

        assertTrue(e.getMessage().contains("no puede ser anterior"));
        verify(cuentaRepository, never()).buscarPorNumero(anyString());
    }
}