package com.jlanzasg.novabank.repository;

import com.jlanzasg.novabank.model.Cliente;
import com.jlanzasg.novabank.model.Cuenta;
import com.jlanzasg.novabank.model.TipoMovimiento;
import com.jlanzasg.novabank.model.Movimiento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.sql.init.mode=never")
@ActiveProfiles("test")
class OperacionRepositoryTest {

    @Autowired
    private OperacionRepository operacionRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Cliente cliente;
    private Cuenta cuenta;

    @BeforeEach
    void setUp() {
        operacionRepository.deleteAll();
        entityManager.clear();

        // Crear cliente
        cliente = Cliente.builder()
                .dni("12345678A")
                .email("test@example.com")
                .telefono("600111222")
                .nombre("Test")
                .apellidos("User")
                .build();
        entityManager.persist(cliente);

        // Crear cuenta
        cuenta = Cuenta.builder()
                .iban("ES91210000000000000001")
                .balance(1000.0)
                .cliente(cliente)
                .build();
        entityManager.persist(cuenta);

        // Crear movimientos
        // OJO: Movimiento.fecha tiene insertable=false/updatable=false, así que el valor de builder se ignora al persistir.
        // Por eso usamos la fecha por defecto (NOW en BD / Hibernate) y filtramos un rango amplio en los tests.
        Movimiento movimiento1 = Movimiento.builder()
                .tipo(TipoMovimiento.DEPOSITO)
                .cantidad(100.0)
                .cuenta(cuenta)
                .build();
        entityManager.persist(movimiento1);

        Movimiento movimiento2 = Movimiento.builder()
                .tipo(TipoMovimiento.RETIRO)
                .cantidad(50.0)
                .cuenta(cuenta)
                .build();
        entityManager.persist(movimiento2);

        entityManager.flush();
    }

    @Test
    void findByCuentaIdAndFechaBetween_Exito_DevuelveMovimientos() {
        // GIVEN
        // Insertable=false en Movimiento.fecha puede delegar el valor a la BD; usamos un rango muy amplio para incluirlo.
        LocalDateTime startDate = LocalDateTime.now().minusYears(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        // WHEN
        List<Movimiento> movimientos = operacionRepository.findByCuentaIdAndFechaBetween(cuenta.getId(), startDate, endDate);

        // THEN
        assertThat(movimientos).isNotNull();
        assertThat(movimientos.size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    void findByCuentaIdAndFechaBetween_RangoFueraDeFechas_DevuelveVacio() {
        // GIVEN
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now().minusDays(20);

        // WHEN
        List<Movimiento> movimientos = operacionRepository.findByCuentaIdAndFechaBetween(cuenta.getId(), startDate, endDate);

        // THEN
        assertThat(movimientos).isEmpty();
    }

    @Test
    void findByCuentaIdAndFechaBetween_CuentaInexistente_DevuelveVacio() {
        // GIVEN
        Long cuentaInexistente = 9999L;
        LocalDateTime startDate = LocalDateTime.now().minusDays(10);
        LocalDateTime endDate = LocalDateTime.now();

        // WHEN
        List<Movimiento> movimientos = operacionRepository.findByCuentaIdAndFechaBetween(cuentaInexistente, startDate, endDate);

        // THEN
        assertThat(movimientos).isEmpty();
    }
}
