package com.jlanzasg.novabank.repository;

import com.jlanzasg.novabank.model.Cliente;
import com.jlanzasg.novabank.model.Cuenta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.sql.init.mode=never")
@ActiveProfiles("test")
class CuentaRepositoryTest {

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Cliente cliente;
    private Cuenta cuenta1;
    private Cuenta cuenta2;

    @BeforeEach
    void setUp() {
        cuentaRepository.deleteAll();
        entityManager.clear();

        cliente = new Cliente();
        cliente.setDni("12345678A");
        cliente.setEmail("test@example.com");
        cliente.setTelefono("600111222");
        cliente.setNombre("Test Name");
        cliente.setApellidos("Test Lastname");
        entityManager.persist(cliente);

        cuenta1 = Cuenta.builder()
                .iban("ES11111111111111111111")
                .balance(1000.0)
                .cliente(cliente)
                .build();
        entityManager.persist(cuenta1);

        cuenta2 = Cuenta.builder()
                .iban("ES22222222222222222222")
                .balance(500.0)
                .cliente(cliente)
                .build();
        entityManager.persist(cuenta2);

        entityManager.flush();
    }

    @Test
    void findByNumeroCuenta_CuentaExiste_DevuelveCuenta() {
        Optional<Cuenta> found = cuentaRepository.findByIban(cuenta1.getIban());
        assertThat(found).isPresent();
        assertThat(found.get().getIban()).isEqualTo(cuenta1.getIban());
    }

    @Test
    void findByNumeroCuenta_CuentaNoExiste_DevuelveVacio() {
        Optional<Cuenta> found = cuentaRepository.findByIban("ES00000000000000000000");
        assertThat(found).isEmpty();
    }

    @Test
    void existsByNumeroCuenta_CuentaExiste_DevuelveTrue() {
        boolean exists = cuentaRepository.existsByIban(cuenta1.getIban());
        assertThat(exists).isTrue();
    }

    @Test
    void existsByNumeroCuenta_CuentaNoExiste_DevuelveFalse() {
        boolean exists = cuentaRepository.existsByIban("ES00000000000000000000");
        assertThat(exists).isFalse();
    }

    @Test
    void findByClienteId_SinCuentas_DevuelveVacio() {
        // Create a new client without accounts
        Cliente clienteSinCuentas = new Cliente();
        clienteSinCuentas.setDni("99999999X");
        clienteSinCuentas.setEmail("noaccounts@example.com");
        clienteSinCuentas.setTelefono("666555444");
        clienteSinCuentas.setNombre("Cliente Sin Cuentas");
        clienteSinCuentas.setApellidos("Apellido");
        entityManager.persist(clienteSinCuentas);
        entityManager.flush();

        Optional<Cuenta> cuentas = cuentaRepository.findByClienteId(clienteSinCuentas.getId());
        assertThat(cuentas).isEmpty();
    }
}
