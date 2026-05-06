package com.jlanzasg.novabank.repository;

import com.jlanzasg.novabank.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * The interface Usuario repository.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    /**
     * Find by email optional.
     *
     * @param email the email
     * @return the optional
     */
    Optional<Usuario> findByEmail(String email);
}