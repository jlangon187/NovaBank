package com.jlanzasg.novabank.cliente.repository;

import com.jlanzasg.novabank.cliente.model.Cliente;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The type Cliente repository test.
 */
@DataJpaTest(properties = "spring.sql.init.mode=never")
@ActiveProfiles("test")
class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Find conflictos returns dni conflict.
     */
    @Test
    void findConflictos_ReturnsDniConflict() {
        Cliente cliente = Cliente.builder()
                .dni("12345678A")
                .nombre("Nombre")
                .apellidos("Apellidos")
                .email("mail@test.com")
                .telefono("600111222")
                .build();
        clienteRepository.save(cliente);

        List<String> conflictos = clienteRepository.findConflictos("12345678A", "otro@mail.com", "699999999");

        assertThat(conflictos).contains("DNI");
    }

    /**
     * Find conflictos when email exists returns email conflict.
     */
    @Test
    void findConflictos_WhenEmailExists_ReturnsEmailConflict() {
        Cliente cliente = Cliente.builder()
                .dni("22222222B")
                .nombre("Ana")
                .apellidos("Diaz")
                .email("ana@test.com")
                .telefono("611111111")
                .build();
        clienteRepository.save(cliente);

        List<String> conflictos = clienteRepository.findConflictos("33333333C", "ana@test.com", "622222222");

        assertThat(conflictos).contains("EMAIL");
    }

    /**
     * Find by dni when not found returns empty.
     */
    @Test
    void findByDni_WhenNotFound_ReturnsEmpty() {
        assertThat(clienteRepository.findByDni("00000000Z")).isEmpty();
    }
}
