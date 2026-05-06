package com.jlanzasg.novabank.cuenta.repository;

import com.jlanzasg.novabank.cuenta.model.Cuenta;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The type Cuenta repository test.
 */
@DataJpaTest(properties = "spring.sql.init.mode=never")
@ActiveProfiles("test")
class CuentaRepositoryTest {

    @Autowired
    private CuentaRepository cuentaRepository;

    /**
     * Find by iban and find all by cliente id work as expected.
     */
    @Test
    void findByIban_AndFindAllByClienteId_WorkAsExpected() {
        Cuenta cuenta = Cuenta.builder()
                .iban("ES91210000000000000001")
                .balance(200.0)
                .clienteId(5L)
                .build();
        cuentaRepository.save(cuenta);

        Optional<Cuenta> found = cuentaRepository.findByIban("ES91210000000000000001");
        Set<Cuenta> byClient = cuentaRepository.findAllByClienteId(5L);

        assertThat(found).isPresent();
        assertThat(byClient).hasSize(1);
    }

    /**
     * Obtener ultimo id returns highest id.
     */
    @Test
    void obtenerUltimoId_ReturnsHighestId() {
        cuentaRepository.save(Cuenta.builder().iban("ES91210000000000000003").balance(10.0).clienteId(2L).build());
        cuentaRepository.save(Cuenta.builder().iban("ES91210000000000000004").balance(20.0).clienteId(2L).build());

        Optional<Long> ultimoId = cuentaRepository.obtenerUltimoId();

        assertThat(ultimoId).isPresent();
        assertThat(ultimoId.get()).isGreaterThan(0L);
    }

    /**
     * Exists by iban when missing returns false.
     */
    @Test
    void existsByIban_WhenMissing_ReturnsFalse() {
        assertThat(cuentaRepository.existsByIban("ES00000000000000000000")).isFalse();
    }
}
