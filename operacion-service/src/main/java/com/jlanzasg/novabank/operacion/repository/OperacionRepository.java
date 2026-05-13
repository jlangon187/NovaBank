package com.jlanzasg.novabank.operacion.repository;

import com.jlanzasg.novabank.operacion.model.Movimiento;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

/**
 * The interface Operacion repository.
 */
@Repository
public interface OperacionRepository extends ReactiveCrudRepository<Movimiento, Long> {

    /**
     * Find by cuenta order by fecha desc list.
     *
     * @param iban the iban
     * @return the list
     */
    Flux<Movimiento> findByIbanOrderByFechaDesc(String iban);

    /**
     * Find by cuenta between order by fecha desc list.
     *
     * @param iban        the iban
     * @param fechaInicio the fecha inicio
     * @param fechaFin    the fecha fin
     * @return the list
     */
    Flux<Movimiento> findByIbanAndFechaBetweenOrderByFechaDesc(String iban, LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
