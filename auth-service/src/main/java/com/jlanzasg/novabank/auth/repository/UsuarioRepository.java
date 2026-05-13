package com.jlanzasg.novabank.auth.repository;

import com.jlanzasg.novabank.auth.model.Usuario;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * The interface Usuario repository.
 */
@Repository
public interface UsuarioRepository extends ReactiveCrudRepository<Usuario, Long> {
    /**
     * Find by email optional.
     *
     * @param email the email
     * @return the optional
     */
    Mono<Usuario> findByEmail(String email);
}