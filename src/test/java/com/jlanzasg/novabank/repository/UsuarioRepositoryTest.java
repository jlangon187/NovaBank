package com.jlanzasg.novabank.repository;

import com.jlanzasg.novabank.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Usuario usuario1;
    private Usuario usuario2;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
        entityManager.clear();

        usuario1 = new Usuario();
        usuario1.setEmail("user1@example.com");
        usuario1.setPassword("password123");
        entityManager.persist(usuario1);

        usuario2 = new Usuario();
        usuario2.setEmail("user2@example.com");
        usuario2.setPassword("password456");
        entityManager.persist(usuario2);

        entityManager.flush();
    }

    @Test
    void findByEmail_UsuarioExiste_DevuelveUsuario() {
        Optional<Usuario> found = usuarioRepository.findByEmail(usuario1.getEmail());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo(usuario1.getEmail());
    }

    @Test
    void findByEmail_UsuarioNoExiste_DevuelveVacio() {
        Optional<Usuario> found = usuarioRepository.findByEmail("nonexistent@example.com");
        assertThat(found).isEmpty();
    }
}
