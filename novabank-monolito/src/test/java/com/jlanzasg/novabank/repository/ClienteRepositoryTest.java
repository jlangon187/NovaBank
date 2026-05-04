package com.jlanzasg.novabank.repository;

import com.jlanzasg.novabank.model.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.sql.init.mode=never")
@ActiveProfiles("test")
class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Cliente cliente1;
    private Cliente cliente2;

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        clienteRepository.deleteAll();

        cliente1 = new Cliente();
        cliente1.setDni("12345678A");
        cliente1.setEmail("test1@example.com");
        cliente1.setTelefono("600111222");
        cliente1.setNombre("Cliente Uno");
        cliente1.setApellidos("Apellido Uno");

        cliente2 = new Cliente();
        cliente2.setDni("87654321B");
        cliente2.setEmail("test2@example.com");
        cliente2.setTelefono("600333444");
        cliente2.setNombre("Cliente Dos");
        cliente2.setApellidos("Apellido Dos");

        entityManager.persist(cliente1);
        entityManager.persist(cliente2);
        entityManager.flush();
    }

    @Test
    void findByDni_ClienteExiste_DevuelveCliente() {
        Optional<Cliente> found = clienteRepository.findByDni(cliente1.getDni());
        assertThat(found).isPresent();
        assertThat(found.get().getDni()).isEqualTo(cliente1.getDni());
    }

    @Test
    void findByDni_ClienteNoExiste_DevuelveVacio() {
        Optional<Cliente> found = clienteRepository.findByDni("00000000Z");
        assertThat(found).isEmpty();
    }

    @Test
    void findConflictos_NoConflictos_DevuelveListaVacia() {
        List<String> conflictos = clienteRepository.findConflictos("99999999X", "new@example.com", "699888777");
        assertThat(conflictos).isEmpty();
    }

    @Test
    void findConflictos_DniDuplicado_DevuelveDni() {
        List<String> conflictos = clienteRepository.findConflictos(cliente1.getDni(), "new@example.com", "699888777");
        assertThat(conflictos).containsExactly("DNI");
    }

    @Test
    void findConflictos_EmailDuplicado_DevuelveEmail() {
        List<String> conflictos = clienteRepository.findConflictos("99999999X", cliente1.getEmail(), "699888777");
        assertThat(conflictos).containsExactly("EMAIL");
    }

    @Test
    void findConflictos_TelefonoDuplicado_DevuelveTelefono() {
        List<String> conflictos = clienteRepository.findConflictos("99999999X", "new@example.com", cliente1.getTelefono());
        assertThat(conflictos).containsExactly("TELEFONO");
    }

    @Test
    void findConflictos_MultiplesDuplicados_DevuelveListaConTodosLosConflictos() {
        List<String> conflictos = clienteRepository.findConflictos(cliente1.getDni(), cliente2.getEmail(), "699888777");
        assertThat(conflictos).containsExactly("DNI", "EMAIL");
    }
}
