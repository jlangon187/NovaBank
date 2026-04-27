package com.jlanzasg.novabank.repository;

import com.jlanzasg.novabank.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The interface Operacion repository.
 */
@Repository
public interface OperacionRepository extends JpaRepository<Movimiento, Long> {
}
