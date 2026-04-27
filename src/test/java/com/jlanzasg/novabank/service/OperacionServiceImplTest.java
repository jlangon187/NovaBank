package com.jlanzasg.novabank.service;

import com.jlanzasg.novabank.repository.MovimientoRepository;
import com.jlanzasg.novabank.service.Impl.OperacionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OperacionServiceImplTest {

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private MovimientoRepository movimientoRepository;

    @InjectMocks
    private OperacionServiceImpl operacionService;

    @Test
    void retirar_conSaldoInsuficiente_debeLanzarExcepcionYDeshacerTransaccion() throws Exception {

        Connection mockConn = mock(Connection.class);

        try (MockedStatic<DatabaseConnectionManager> dummyDb = mockStatic(DatabaseConnectionManager.class)) {
            dummyDb.when(DatabaseConnectionManager::getConexion).thenReturn(mockConn);

            Cuenta cuentaOrigen = Cuenta.builder()
                    .id(1L)
                    .iban("ES91210000000000000001")
                    .balance(100.0)
                    .build();

            when(cuentaRepository.buscarPorNumero(eq("ES91210000000000000001"), eq(mockConn)))
                    .thenReturn(Optional.of(cuentaOrigen));

            RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
                operacionService.retirar("ES91210000000000000001", 500.0);
            });

            assertTrue(excepcion.getMessage().contains("Saldo insuficiente"));

            verify(cuentaRepository, never()).actualizarSaldo(any(), any(), any());
            verify(movimientoRepository, never()).guardar(any(), any());

            verify(mockConn, times(1)).rollback();
        }
    }
}