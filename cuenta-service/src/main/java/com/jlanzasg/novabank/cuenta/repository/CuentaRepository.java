package com.jlanzasg.novabank.cuenta.repository;

import com.jlanzasg.novabank.cuenta.model.Cuenta;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The interface Cuenta repository.
 */
@Repository
public interface CuentaRepository extends ReactiveCrudRepository<Cuenta, Long> {

    /**
     * Exists by iban mono.
     *
     * @param iban the iban
     * @return the mono
     */
    Mono<Boolean> existsByIban(String iban);

    /**
     * Find by iban mono.
     *
     * @param iban the iban
     * @return the mono
     */
    Mono<Cuenta> findByIban(String iban);

    /**
     * Find all by cliente id flux.
     *
     * @param clienteId the cliente id
     * @return the flux
     */
    Flux<Cuenta> findAllByClienteId(Long clienteId);

    /**
     * Obtener ultimo id mono.
     *
     * @return the mono
     */
    @Query("SELECT MAX(id) FROM cuentas")
    Mono<Long> obtenerUltimoId();
}
