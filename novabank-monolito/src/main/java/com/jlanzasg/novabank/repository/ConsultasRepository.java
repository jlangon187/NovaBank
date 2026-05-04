package com.jlanzasg.novabank.repository;

import com.jlanzasg.novabank.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The interface Movimiento repository.
 */
@Repository
public interface ConsultasRepository extends JpaRepository<Movimiento, Long> {

    /**
     * Find by cuenta order by fecha desc list.
     *
     * @param cuentaId the cuenta id
     * @return the list
     */
    List<Movimiento> findByCuentaIdOrderByFechaDesc(Long cuentaId);

    /**
     * Find by cuenta between order by fecha desc list.
     *
     * @param cuentaId    the cuenta id
     * @param fechaInicio the fecha inicio
     * @param fechaFin    the fecha fin
     * @return the list
     */
    List<Movimiento> findByCuentaIdAndFechaBetweenOrderByFechaDesc(Long cuentaId, LocalDateTime fechaInicio, LocalDateTime fechaFin);


    /**
     * Find saldo by cuenta iban string.
     *
     * @param iban the iban
     * @return the string
     */
    String findSaldoByCuentaIban(String iban);
}
