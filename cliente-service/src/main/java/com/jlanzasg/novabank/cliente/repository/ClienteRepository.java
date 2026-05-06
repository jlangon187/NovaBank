package com.jlanzasg.novabank.cliente.repository;


import com.jlanzasg.novabank.cliente.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * The interface Cliente repository.
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /**
     * Find by dni optional.
     *
     * @param dni the dni
     * @return the optional
     */
    Optional<Cliente> findByDni(String dni);

    /**
     * Hace 1 sola llamada a la BD.
     * Si encuentra coincidencias, devuelve 'DNI', 'EMAIL' o 'TELEFONO'.
     * Si no encuentra nada, devuelve una lista vacía.
     *
     * @param dni      the dni
     * @param email    the email
     * @param telefono the telefono
     * @return the list
     */
    @Query("SELECT CASE " +
            "WHEN c.dni = :dni THEN 'DNI' " +
            "WHEN c.email = :email THEN 'EMAIL' " +
            "WHEN c.telefono = :telefono THEN 'TELEFONO' " +
            "ELSE 'UNKNOWN' END " +
            "FROM Cliente c " +
            "WHERE c.dni = :dni OR c.email = :email OR c.telefono = :telefono")
    List<String> findConflictos(
            @Param("dni") String dni,
            @Param("email") String email,
            @Param("telefono") String telefono
    );
}