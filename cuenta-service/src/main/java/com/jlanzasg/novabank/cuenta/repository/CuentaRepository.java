package com.jlanzasg.novabank.cuenta.repository;

import com.jlanzasg.novabank.cuenta.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * The interface Cuenta repository.
 */
@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

    /**
     * Exists by iban boolean.
     *
     * @param iban the iban
     * @return the boolean
     */
    boolean existsByIban(String iban);

    /**
     * Find by iban optional.
     *
     * @param iban the iban
     * @return the optional
     */
    Optional<Cuenta> findByIban(String iban);

    /**
     * Find all by cliente id set.
     *
     * @param clienteId the cliente id
     * @return the set
     */
    Set<Cuenta> findAllByClienteId(Long clienteId);

    /**
     * Query to obtain the last id optional.
     *
     * @return the optional
     */
    @Query("SELECT MAX(c.id) FROM Cuenta c")
    Optional<Long> obtenerUltimoId();
}
