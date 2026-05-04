package com.jlanzasg.novabank.repository;

import com.jlanzasg.novabank.model.Cliente;
import com.jlanzasg.novabank.model.Cuenta;
import com.jlanzasg.novabank.model.Movimiento;
import com.jlanzasg.novabank.model.TipoMovimiento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ConsultasRepositoryTest {

    @Autowired
    private ConsultasRepository consultasRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Cliente cliente;
    private Cuenta cuenta;
    private Movimiento movimiento1;
    private Movimiento movimiento2;

    @BeforeEach
    void setUp() {
        consultasRepository.deleteAll();
        entityManager.clear();

        cliente = new Cliente();
        cliente.setDni("12345678A");
        cliente.setEmail("test@example.com");
        cliente.setTelefono("600111222");
        cliente.setNombre("Test Name");
        cliente.setApellidos("Test Lastname");
        entityManager.persist(cliente);

        cuenta = Cuenta.builder()
                .iban("ES12345678901234567890")
                .balance(1000.0)
                .cliente(cliente)
                .build();
        entityManager.persist(cuenta);

        movimiento1 = Movimiento.builder()
                .tipo(TipoMovimiento.DEPOSITO)
                .cantidad(100.0)
                .fecha(LocalDateTime.now().minusDays(5))
                .cuenta(cuenta)
                .build();
        entityManager.persist(movimiento1);

        movimiento2 = Movimiento.builder()
                .tipo(TipoMovimiento.RETIRO)
                .cantidad(50.0)
                .fecha(LocalDateTime.now().minusDays(1))
                .cuenta(cuenta)
                .build();
        entityManager.persist(movimiento2);

        entityManager.flush();
    }

    @Test
    void findByCuentaIdAndFechaBetweenOrderByFechaDesc_Exito_DevuelveMovimientos() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(10);
        LocalDateTime endDate = LocalDateTime.now();

        List<Movimiento> movimientos =
                consultasRepository.findByCuentaIdAndFechaBetweenOrderByFechaDesc(cuenta.getId(), startDate, endDate);

        assertThat(movimientos).hasSize(2);
        // order desc por fecha => movimiento2 (más reciente) primero
        assertThat(movimientos.get(0).getId()).isEqualTo(movimiento2.getId());
        assertThat(movimientos.get(1).getId()).isEqualTo(movimiento1.getId());
    }

    @Test
    void findByCuentaIdAndFechaBetweenOrderByFechaDesc_SinResultados_DevuelveVacio() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(20);
        LocalDateTime endDate = LocalDateTime.now().minusDays(15);

        List<Movimiento> movimientos =
                consultasRepository.findByCuentaIdAndFechaBetweenOrderByFechaDesc(cuenta.getId(), startDate, endDate);

        assertThat(movimientos).isEmpty();
    }

    @Test
    void findByCuentaIdOrderByFechaDesc_DevuelveMovimientosOrdenados() {
        List<Movimiento> movimientos = consultasRepository.findByCuentaIdOrderByFechaDesc(cuenta.getId());

        assertThat(movimientos).hasSize(2);
        assertThat(movimientos.get(0).getId()).isEqualTo(movimiento2.getId());
        assertThat(movimientos.get(1).getId()).isEqualTo(movimiento1.getId());
    }
}
