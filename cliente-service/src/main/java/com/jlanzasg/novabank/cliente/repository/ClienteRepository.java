package com.jlanzasg.novabank.cliente.repository;


import com.jlanzasg.novabank.cliente.model.Cliente;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The interface Cliente repository.
 */
@Repository
public interface ClienteRepository extends ReactiveCrudRepository<Cliente, Long> {

    /**
     * Find by dni optional.
     *
     * @param dni the dni
     * @return the optional
     */
    Mono<Cliente> findByDni(String dni);

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
            "WHEN \"DNI\" = :dni THEN 'DNI' " +
            "WHEN \"EMAIL\" = :email THEN 'EMAIL' " +
            "WHEN \"TELEFONO\" = :telefono THEN 'TELEFONO' " +
            "ELSE 'UNKNOWN' END " +
            "FROM \"clientes\" " +
            "WHERE \"DNI\" = :dni OR \"EMAIL\" = :email OR \"TELEFONO\" = :telefono")
    Flux<String> findConflictos(
            @Param("dni") String dni,
            @Param("email") String email,
            @Param("telefono") String telefono
    );
}
