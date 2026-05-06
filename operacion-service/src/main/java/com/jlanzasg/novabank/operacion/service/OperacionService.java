package com.jlanzasg.novabank.operacion.service;

import com.jlanzasg.novabank.operacion.client.CuentaClient;
import com.jlanzasg.novabank.operacion.dto.cuenta.response.CuentaResponseDTO;
import com.jlanzasg.novabank.operacion.dto.cuenta.response.CuentaSaldoResponseDTO;
import com.jlanzasg.novabank.operacion.dto.operacion.request.OperacionRequestDTO;
import com.jlanzasg.novabank.operacion.dto.operacion.request.TransferenciaRequestDTO;
import com.jlanzasg.novabank.operacion.dto.operacion.response.MovimientoResponseDTO;
import com.jlanzasg.novabank.operacion.exception.DuplicateException;
import com.jlanzasg.novabank.operacion.exception.NotFoundException;
import com.jlanzasg.novabank.operacion.exception.SaldoInsuficienteException;
import com.jlanzasg.novabank.operacion.exception.ServiceException;
import com.jlanzasg.novabank.operacion.mapper.impl.CuentaMapper;
import com.jlanzasg.novabank.operacion.mapper.impl.OperacionMapper;
import com.jlanzasg.novabank.operacion.model.Movimiento;
import com.jlanzasg.novabank.operacion.model.TipoMovimiento;
import com.jlanzasg.novabank.operacion.repository.OperacionRepository;
import feign.FeignException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Operacion service.
 */
@Service
public class OperacionService {

    private final OperacionRepository operacionRepository;
    private final CuentaClient cuentaClient;
    private final OperacionMapper operacionMapper;
    private final CuentaMapper cuentaMapper;

    /**
     * Instantiates a new Operacion service.
     *
     * @param operacionRepository the operacion repository
     * @param cuentaRepository    the cuenta repository
     * @param cuentaClient        the cuenta client
     * @param operacionMapper     the operacion mapper
     * @param cuentaMapper        the cuenta mapper
     */
    public OperacionService(OperacionRepository operacionRepository, CuentaClient cuentaRepository, CuentaClient cuentaClient, OperacionMapper operacionMapper, CuentaMapper cuentaMapper) {
        this.operacionRepository = operacionRepository;
        this.cuentaClient = cuentaClient;
        this.operacionMapper = operacionMapper;
        this.cuentaMapper = cuentaMapper;
    }

    /**
     * Depositar movimiento response dto.
     *
     * @param dto the dto
     * @return the movimiento response dto
     */
    @Transactional
    public MovimientoResponseDTO depositar(OperacionRequestDTO dto) {

        if (dto.getImporte() <= 0) {
            throw new IllegalArgumentException("El importe del depósito debe ser mayor que cero.");
        }

        CuentaResponseDTO cuentaDTO;

        try {
            cuentaDTO = cuentaClient.getCuentaByIban(dto.getIbanCuenta());
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("La cuenta con IBAN " + dto.getIbanCuenta() + " no existe.");
        }

        cuentaDTO.setBalance(cuentaDTO.getBalance() + dto.getImporte());

        try {
            cuentaClient.actualizarSaldo(cuentaDTO.getIban(), cuentaDTO.getBalance());
        }  catch (FeignException e) {
            throw new ServiceException("El servicio de operaciones falló al actualizar el saldo de la cuenta con IBAN " + dto.getIbanCuenta() + ". Detalles: " + e.getMessage());
        }

        Movimiento movimiento = operacionMapper.toEntity(dto);
        movimiento.setTipo(TipoMovimiento.DEPOSITO);
        movimiento.setCuentaIban(dto.getIbanCuenta());
        Movimiento movimientoGuardado = operacionRepository.save(movimiento);

        return operacionMapper.toResponseDTO(movimientoGuardado);
    }

    /**
     * Retirar movimiento response dto.
     *
     * @param dto the dto
     * @return the movimiento response dto
     */
    @Transactional
    public MovimientoResponseDTO retirar(OperacionRequestDTO dto) {

        if (dto.getImporte() <= 0) {
            throw new IllegalArgumentException("El importe de la retirada debe ser mayor que cero.");
        }

        CuentaResponseDTO cuentaDTO;

        try {
            cuentaDTO = cuentaClient.getCuentaByIban(dto.getIbanCuenta());
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("La cuenta con IBAN " + dto.getIbanCuenta() + " no existe.");
        }

        if (cuentaDTO.getBalance() < dto.getImporte()) {
            throw new SaldoInsuficienteException("Fondos insuficientes para realizar la retirada. Balance actual: " + cuentaDTO.getBalance());
        }

        cuentaDTO.setBalance(cuentaDTO.getBalance() - dto.getImporte());

        try {
            cuentaClient.actualizarSaldo(cuentaDTO.getIban(), cuentaDTO.getBalance());
        }  catch (FeignException e) {
            throw new ServiceException("El servicio de operaciones falló al actualizar el saldo de la cuenta con IBAN " + dto.getIbanCuenta() + ". Detalles: " + e.getMessage());
        }

        Movimiento movimiento = operacionMapper.toEntity(dto);
        movimiento.setTipo(TipoMovimiento.RETIRO);
        movimiento.setCuentaIban(dto.getIbanCuenta());
        Movimiento movimientoGuardado = operacionRepository.save(movimiento);

        return operacionMapper.toResponseDTO(movimientoGuardado);
    }

    /**
     * Transferir list.
     *
     * @param dto the dto
     * @return the list
     */
    @Transactional
    public List<MovimientoResponseDTO> transferir(TransferenciaRequestDTO dto) {

        if (dto.getImporte() <= 0) {
            throw new IllegalArgumentException("El importe de la transferencia debe ser mayor que cero.");
        }

        if (dto.getCuentaOrigen().equals(dto.getCuentaDestino())) {
            throw new DuplicateException("No se puede realizar una transferencia a la misma cuenta.");
        }

        CuentaResponseDTO cuentaOrigenDTO;

        try {
            cuentaOrigenDTO = cuentaClient.getCuentaByIban(dto.getCuentaOrigen());
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("La cuenta con IBAN " + dto.getCuentaOrigen() + " no existe.");
        }

        CuentaResponseDTO cuentaDestinoDTO;

        try {
            cuentaDestinoDTO = cuentaClient.getCuentaByIban(dto.getCuentaDestino());
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("La cuenta con IBAN " + dto.getCuentaDestino() + " no existe.");
        }

        if (cuentaOrigenDTO.getBalance() < dto.getImporte()) {
            throw new SaldoInsuficienteException("Fondos insuficientes para realizar la transferencia. Balance actual: " + cuentaOrigenDTO.getBalance());
        }

        cuentaOrigenDTO.setBalance(cuentaOrigenDTO.getBalance() - dto.getImporte());
        cuentaDestinoDTO.setBalance(cuentaDestinoDTO.getBalance() + dto.getImporte());

        try {
            cuentaClient.actualizarSaldo(cuentaOrigenDTO.getIban(), cuentaOrigenDTO.getBalance());
        } catch (FeignException e) {
            throw new ServiceException("Fallo de red al retirar el dinero del origen. Transferencia cancelada.");
        }

        try {
            cuentaClient.actualizarSaldo(cuentaDestinoDTO.getIban(), cuentaDestinoDTO.getBalance());
        } catch (FeignException e) {
            try {
                Double saldoRestaurado = cuentaOrigenDTO.getBalance() + dto.getImporte();
                cuentaClient.actualizarSaldo(cuentaOrigenDTO.getIban(), saldoRestaurado);
            } catch (FeignException ex) {
                throw new ServiceException("Error crítico: No se pudo restaurar el saldo de la cuenta origen después de un fallo al actualizar el destino. Detalles: " + ex.getMessage());
            }

            throw new ServiceException("Error al ingresar en destino. La transferencia ha sido cancelada y los fondos devueltos al origen.");
        }

        Movimiento movimientoOrigen = Movimiento.builder()
                .tipo(TipoMovimiento.TRANSFERENCIA_SALIENTE)
                .cantidad(dto.getImporte())
                .cuentaIban(dto.getCuentaOrigen())
                .build();

        Movimiento movimientoDestino = Movimiento.builder()
                .tipo(TipoMovimiento.TRANSFERENCIA_ENTRANTE)
                .cantidad(dto.getImporte())
                .cuentaIban(dto.getCuentaDestino())
                .build();

        Movimiento movimientoOrigenGuardado = operacionRepository.save(movimientoOrigen);
        Movimiento movimientoDestinoGuardado = operacionRepository.save(movimientoDestino);

        return List.of(
                operacionMapper.toResponseDTO(movimientoOrigenGuardado),
                operacionMapper.toResponseDTO(movimientoDestinoGuardado)
        );
    }

    /**
     * Obtener movimientos por cuenta y fecha list.
     *
     * @param iban        the iban
     * @param fechaInicio the fecha inicio
     * @param fechaFin    the fecha fin
     * @return the list
     */
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<MovimientoResponseDTO> obtenerMovimientosPorCuentaYFecha(String iban, LocalDateTime
            fechaInicio, LocalDateTime fechaFin) {

        List<Movimiento> movimiento;

        if (fechaInicio != null && fechaFin != null) {
            movimiento = operacionRepository.findByCuentaIbanAndFechaBetweenOrderByFechaDesc(iban, fechaInicio, fechaFin);
        } else {
            movimiento = operacionRepository.findByCuentaIbanOrderByFechaDesc(iban);
        }

        return movimiento.stream()
                .map(operacionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Consultar saldo.
     *
     * @param iban the iban
     * @return the cuenta saldo response dto
     */
    public CuentaSaldoResponseDTO consultarSaldo(String iban) {

        CuentaResponseDTO cuenta;

        try {
            cuenta = cuentaClient.getCuentaByIban(iban);
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("La cuenta con IBAN " + iban + " no existe.");
        }
        return cuentaMapper.toSaldoResponseDTO(cuenta);
    }
}