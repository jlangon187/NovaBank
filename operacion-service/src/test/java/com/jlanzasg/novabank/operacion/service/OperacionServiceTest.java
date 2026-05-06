package com.jlanzasg.novabank.operacion.service;

import com.jlanzasg.novabank.operacion.client.CuentaClient;
import com.jlanzasg.novabank.operacion.dto.cuenta.response.CuentaResponseDTO;
import com.jlanzasg.novabank.operacion.dto.operacion.request.OperacionRequestDTO;
import com.jlanzasg.novabank.operacion.dto.operacion.request.TransferenciaRequestDTO;
import com.jlanzasg.novabank.operacion.dto.operacion.response.MovimientoResponseDTO;
import com.jlanzasg.novabank.operacion.exception.SaldoInsuficienteException;
import com.jlanzasg.novabank.operacion.exception.ServiceException;
import com.jlanzasg.novabank.operacion.exception.DuplicateException;
import com.jlanzasg.novabank.operacion.exception.NotFoundException;
import com.jlanzasg.novabank.operacion.mapper.impl.CuentaMapper;
import com.jlanzasg.novabank.operacion.mapper.impl.OperacionMapper;
import com.jlanzasg.novabank.operacion.model.Movimiento;
import com.jlanzasg.novabank.operacion.model.TipoMovimiento;
import com.jlanzasg.novabank.operacion.repository.OperacionRepository;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The type Operacion service test.
 */
@ExtendWith(MockitoExtension.class)
class OperacionServiceTest {

    @Mock
    private OperacionRepository operacionRepository;
    @Mock
    private CuentaClient cuentaClient;
    @Mock
    private OperacionMapper operacionMapper;
    @Mock
    private CuentaMapper cuentaMapper;
    @InjectMocks
    private OperacionService operacionService;

    /**
     * Depositar when valid updates balance and returns movement.
     */
    @Test
    void depositar_WhenValid_UpdatesBalanceAndReturnsMovement() {
        OperacionRequestDTO request = new OperacionRequestDTO();
        request.setIbanCuenta("ES91210000000000000001");
        request.setImporte(100.0);

        CuentaResponseDTO cuenta = new CuentaResponseDTO();
        cuenta.setIban("ES91210000000000000001");
        cuenta.setBalance(200.0);

        Movimiento entity = Movimiento.builder().tipo(TipoMovimiento.DEPOSITO).cantidad(100.0).cuentaIban(cuenta.getIban()).build();
        MovimientoResponseDTO response = new MovimientoResponseDTO();
        response.setTipoMovimiento("DEPOSITO");

        when(cuentaClient.getCuentaByIban(request.getIbanCuenta())).thenReturn(cuenta);
        when(operacionMapper.toEntity(request)).thenReturn(entity);
        when(operacionRepository.save(any(Movimiento.class))).thenReturn(entity);
        when(operacionMapper.toResponseDTO(entity)).thenReturn(response);

        MovimientoResponseDTO result = operacionService.depositar(request);

        assertEquals("DEPOSITO", result.getTipoMovimiento());
    }

    /**
     * Retirar when insufficient balance throws exception.
     */
    @Test
    void retirar_WhenInsufficientBalance_ThrowsException() {
        OperacionRequestDTO request = new OperacionRequestDTO();
        request.setIbanCuenta("ES91210000000000000001");
        request.setImporte(500.0);

        CuentaResponseDTO cuenta = new CuentaResponseDTO();
        cuenta.setIban("ES91210000000000000001");
        cuenta.setBalance(200.0);

        when(cuentaClient.getCuentaByIban(request.getIbanCuenta())).thenReturn(cuenta);

        assertThrows(SaldoInsuficienteException.class, () -> operacionService.retirar(request));
    }

    /**
     * Transferir when destination update fails restores origin and throws service exception.
     */
    @Test
    void transferir_WhenDestinationUpdateFails_RestoresOriginAndThrowsServiceException() {
        TransferenciaRequestDTO request = new TransferenciaRequestDTO();
        request.setCuentaOrigen("ES91210000000000000001");
        request.setCuentaDestino("ES91210000000000000002");
        request.setImporte(50.0);

        CuentaResponseDTO origen = new CuentaResponseDTO();
        origen.setIban("ES91210000000000000001");
        origen.setBalance(200.0);

        CuentaResponseDTO destino = new CuentaResponseDTO();
        destino.setIban("ES91210000000000000002");
        destino.setBalance(100.0);

        when(cuentaClient.getCuentaByIban(request.getCuentaOrigen())).thenReturn(origen);
        when(cuentaClient.getCuentaByIban(request.getCuentaDestino())).thenReturn(destino);

        doNothing().doNothing().when(cuentaClient).actualizarSaldo(eq("ES91210000000000000001"), anyDouble());
        doThrow(mock(FeignException.InternalServerError.class))
                .when(cuentaClient).actualizarSaldo(eq("ES91210000000000000002"), anyDouble());

        assertThrows(ServiceException.class, () -> operacionService.transferir(request));

        verify(cuentaClient, times(2)).actualizarSaldo(eq("ES91210000000000000001"), anyDouble());
        verify(cuentaClient, times(1)).actualizarSaldo(eq("ES91210000000000000002"), anyDouble());
        verify(operacionRepository, never()).save(any(Movimiento.class));
    }

    /**
     * Transferir when same origin and destination throws duplicate exception.
     */
    @Test
    void transferir_WhenSameOriginAndDestination_ThrowsDuplicateException() {
        TransferenciaRequestDTO request = new TransferenciaRequestDTO();
        request.setCuentaOrigen("ES91210000000000000001");
        request.setCuentaDestino("ES91210000000000000001");
        request.setImporte(10.0);

        assertThrows(DuplicateException.class, () -> operacionService.transferir(request));
        verifyNoInteractions(cuentaClient);
    }

    /**
     * Consultar saldo when account not found throws not found exception.
     */
    @Test
    void consultarSaldo_WhenAccountNotFound_ThrowsNotFoundException() {
        when(cuentaClient.getCuentaByIban("ES404")).thenThrow(mock(FeignException.NotFound.class));

        assertThrows(NotFoundException.class, () -> operacionService.consultarSaldo("ES404"));
    }

    /**
     * Obtener movimientos por cuenta y fecha with range uses range repository method.
     */
    @Test
    void obtenerMovimientosPorCuentaYFecha_WithRange_UsesRangeRepositoryMethod() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fin = LocalDateTime.now();
        Movimiento movimiento = Movimiento.builder().cuentaIban("ES1").cantidad(20.0).tipo(TipoMovimiento.DEPOSITO).build();
        MovimientoResponseDTO response = new MovimientoResponseDTO();
        response.setCantidad(20.0);

        when(operacionRepository.findByCuentaIbanAndFechaBetweenOrderByFechaDesc("ES1", inicio, fin)).thenReturn(List.of(movimiento));
        when(operacionMapper.toResponseDTO(movimiento)).thenReturn(response);

        List<MovimientoResponseDTO> result = operacionService.obtenerMovimientosPorCuentaYFecha("ES1", inicio, fin);

        assertEquals(1, result.size());
        assertEquals(20.0, result.get(0).getCantidad());
    }

    /**
     * Depositar when amount is zero throws illegal argument exception.
     */
    @Test
    void depositar_WhenAmountIsZero_ThrowsIllegalArgumentException() {
        OperacionRequestDTO request = new OperacionRequestDTO();
        request.setIbanCuenta("ES91210000000000000001");
        request.setImporte(0.0);

        assertThrows(IllegalArgumentException.class, () -> operacionService.depositar(request));
        verifyNoInteractions(cuentaClient);
    }

    /**
     * Transferir when origin update fails throws service exception.
     */
    @Test
    void transferir_WhenOriginUpdateFails_ThrowsServiceException() {
        TransferenciaRequestDTO request = new TransferenciaRequestDTO();
        request.setCuentaOrigen("ES91210000000000000001");
        request.setCuentaDestino("ES91210000000000000002");
        request.setImporte(25.0);

        CuentaResponseDTO origen = new CuentaResponseDTO();
        origen.setIban("ES91210000000000000001");
        origen.setBalance(200.0);

        CuentaResponseDTO destino = new CuentaResponseDTO();
        destino.setIban("ES91210000000000000002");
        destino.setBalance(100.0);

        when(cuentaClient.getCuentaByIban(request.getCuentaOrigen())).thenReturn(origen);
        when(cuentaClient.getCuentaByIban(request.getCuentaDestino())).thenReturn(destino);
        doThrow(mock(FeignException.InternalServerError.class)).when(cuentaClient)
                .actualizarSaldo(eq("ES91210000000000000001"), anyDouble());

        assertThrows(ServiceException.class, () -> operacionService.transferir(request));
        verify(cuentaClient, never()).actualizarSaldo(eq("ES91210000000000000002"), anyDouble());
    }
}
