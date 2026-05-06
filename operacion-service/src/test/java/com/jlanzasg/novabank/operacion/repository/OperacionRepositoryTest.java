package com.jlanzasg.novabank.operacion.repository;

import com.jlanzasg.novabank.operacion.model.Movimiento;
import com.jlanzasg.novabank.operacion.model.TipoMovimiento;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The type Operacion repository test.
 */
@DataJpaTest(properties = "spring.sql.init.mode=never")
@ActiveProfiles("test")
class OperacionRepositoryTest {

    @Autowired
    private OperacionRepository operacionRepository;

    /**
     * Find by cuenta iban order by fecha desc returns rows.
     */
    @Test
    void findByCuentaIbanOrderByFechaDesc_ReturnsRows() {
        operacionRepository.save(Movimiento.builder().cuentaIban("ES1").tipo(TipoMovimiento.DEPOSITO).cantidad(10.0).build());
        operacionRepository.save(Movimiento.builder().cuentaIban("ES1").tipo(TipoMovimiento.RETIRO).cantidad(5.0).build());

        List<Movimiento> result = operacionRepository.findByCuentaIbanOrderByFechaDesc("ES1");

        assertThat(result).hasSize(2);
    }

    /**
     * Find by cuenta iban and fecha between order by fecha desc filters by range.
     */
    @Test
    void findByCuentaIbanAndFechaBetweenOrderByFechaDesc_FiltersByRange() {
        operacionRepository.save(Movimiento.builder().cuentaIban("ES2").tipo(TipoMovimiento.DEPOSITO).cantidad(15.0).build());

        List<Movimiento> result = operacionRepository.findByCuentaIbanAndFechaBetweenOrderByFechaDesc(
                "ES2",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1));

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getCuentaIban()).isEqualTo("ES2");
    }
}
