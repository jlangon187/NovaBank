package com.jlanzasg.novabank.operacion.repository;

import com.jlanzasg.novabank.operacion.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The interface Operacion repository.
 */
@Repository
public interface OperacionRepository extends JpaRepository<Movimiento, Long> {

    /**
     * Find by cuenta order by fecha desc list.
     *
     * @param iban the iban
     * @return the list
     */
    List<Movimiento> findByCuentaIbanOrderByFechaDesc(String iban);

    /**
     * Find by cuenta between order by fecha desc list.
     *
     * @param iban        the iban
     * @param fechaInicio the fecha inicio
     * @param fechaFin    the fecha fin
     * @return the list
     */
    List<Movimiento> findByCuentaIbanAndFechaBetweenOrderByFechaDesc(String iban, LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
