package com.jlanzasg.novabank.service;

import com.jlanzasg.novabank.dto.operacion.response.MovimientoResponseDTO;
import com.jlanzasg.novabank.mapper.impl.OperacionMapper;
import com.jlanzasg.novabank.model.Movimiento;
import com.jlanzasg.novabank.repository.ConsultasRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Consulta service.
 */
@Service
public class ConsultaService {

    private final ConsultasRepository consultasRepository;
    private final OperacionMapper operacionMapper;

    /**
     * Instantiates a new Consulta service.
     *
     * @param consultasRepository the consultas repository
     * @param operacionMapper     the operacion mapper
     */
    public ConsultaService(ConsultasRepository consultasRepository, OperacionMapper operacionMapper) {
        this.consultasRepository = consultasRepository;
        this.operacionMapper = operacionMapper;
    }

    /**
     * Obtener movimientos por cuenta y fecha list.
     *
     * @param cuentaId    the cuenta id
     * @param fechaInicio the fecha inicio
     * @param fechaFin    the fecha fin
     * @return the list
     */
    @Transactional(readOnly = true)
    public List<MovimientoResponseDTO> obtenerMovimientosPorCuentaYFecha(Long cuentaId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {

        List<Movimiento> movimiento;

        if (fechaInicio != null && fechaFin != null) {
            movimiento = consultasRepository.findByCuentaIdAndFechaBetweenOrderByFechaDesc(cuentaId, fechaInicio, fechaFin);
        } else {
            movimiento = consultasRepository.findByCuentaIdOrderByFechaDesc(cuentaId);
        }

        return movimiento.stream()
                .map(operacionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
