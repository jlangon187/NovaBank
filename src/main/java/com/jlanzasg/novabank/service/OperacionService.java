package com.jlanzasg.novabank.service;

import com.jlanzasg.novabank.dto.operacion.request.OperacionRequestDTO;
import com.jlanzasg.novabank.dto.operacion.request.TransferenciaRequestDTO;
import com.jlanzasg.novabank.dto.operacion.response.MovimientoResponseDTO;
import com.jlanzasg.novabank.exception.DuplicateException;
import com.jlanzasg.novabank.exception.NotFoundException;
import com.jlanzasg.novabank.exception.SaldoInsuficienteException;
import com.jlanzasg.novabank.mapper.impl.OperacionMapper;
import com.jlanzasg.novabank.model.Cuenta;
import com.jlanzasg.novabank.model.Movimiento;
import com.jlanzasg.novabank.model.TipoMovimiento;
import com.jlanzasg.novabank.repository.CuentaRepository;
import com.jlanzasg.novabank.repository.OperacionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The type Operacion service.
 */
@Service
public class OperacionService {

    private final OperacionRepository operacionRepository;
    private final CuentaRepository cuentaRepository;
    private final OperacionMapper operacionMapper;

    /**
     * Instantiates a new Operacion service.
     *
     * @param operacionRepository  the operacion repository
     * @param cuentaRepository     the cuenta repository
     * @param cuentaService        the cuenta service
     * @param operacionMapper      the operacion mapper
     */
    public OperacionService(OperacionRepository operacionRepository, CuentaRepository cuentaRepository,
                            CuentaService cuentaService, OperacionMapper operacionMapper) {
        this.operacionRepository = operacionRepository;
        this.cuentaRepository = cuentaRepository;
        this.operacionMapper = operacionMapper;
    }

    /**
     * Depositar operacion response dto.
     *
     * @param dto the dto
     * @return the movimiento response dto
     */
    @Transactional
    public MovimientoResponseDTO depositar(OperacionRequestDTO dto) {

        if (dto.getImporte() <= 0) {
            throw new IllegalArgumentException("El importe del depósito debe ser mayor que cero.");
        }

        Cuenta cuenta = cuentaRepository.findByIban(dto.getIbanCuenta())
                .orElseThrow(() -> new NotFoundException("La cuenta con IBAN " + dto.getIbanCuenta() + " no existe."));

        cuenta.setBalance(cuenta.getBalance() + dto.getImporte());
        cuentaRepository.save(cuenta);

        Movimiento movimiento = operacionMapper.toEntity(dto);
        movimiento.setTipo(TipoMovimiento.DEPOSITO);
        movimiento.setCuenta(cuenta);
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

        Cuenta cuenta = cuentaRepository.findByIban(dto.getIbanCuenta())
                .orElseThrow(() -> new NotFoundException("La cuenta con IBAN " + dto.getIbanCuenta() + " no existe."));

        if (cuenta.getBalance() < dto.getImporte()) {
            throw new SaldoInsuficienteException("Fondos insuficientes para realizar la retirada. Balance actual: " + cuenta.getBalance());
        }

        cuenta.setBalance(cuenta.getBalance() - dto.getImporte());
        cuentaRepository.save(cuenta);

        Movimiento movimiento = operacionMapper.toEntity(dto);
        movimiento.setTipo(TipoMovimiento.RETIRO);
        movimiento.setCuenta(cuenta);
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

        Cuenta cuentaOrigen = cuentaRepository.findByIban(dto.getCuentaOrigen())
                .orElseThrow(() -> new NotFoundException("La cuenta de origen con IBAN " + dto.getCuentaOrigen() + " no existe."));

        Cuenta cuentaDestino = cuentaRepository.findByIban(dto.getCuentaDestino())
                .orElseThrow(() -> new NotFoundException("La cuenta de destino con IBAN " + dto.getCuentaDestino() + " no existe."));

        if (cuentaOrigen.getBalance() < dto.getImporte()) {
            throw new SaldoInsuficienteException("Fondos insuficientes para realizar la transferencia. Balance actual: " + cuentaOrigen.getBalance());
        }

        cuentaOrigen.setBalance(cuentaOrigen.getBalance() - dto.getImporte());
        cuentaDestino.setBalance(cuentaDestino.getBalance() + dto.getImporte());

        cuentaRepository.save(cuentaOrigen);
        cuentaRepository.save(cuentaDestino);

        Movimiento movimientoOrigen = Movimiento.builder()
                .tipo(TipoMovimiento.TRANSFERENCIA_SALIENTE)
                .cantidad(dto.getImporte())
                .cuenta(cuentaOrigen)
                .build();

        Movimiento movimientoDestino = Movimiento.builder()
                .tipo(TipoMovimiento.TRANSFERENCIA_ENTRANTE)
                .cantidad(dto.getImporte())
                .cuenta(cuentaDestino)
                .build();

        Movimiento movimientoOrigenGuardado = operacionRepository.save(movimientoOrigen);
        Movimiento movimientoDestinoGuardado = operacionRepository.save(movimientoDestino);

        return List.of(
                operacionMapper.toResponseDTO(movimientoOrigenGuardado),
                operacionMapper.toResponseDTO(movimientoDestinoGuardado)
        );
    }
}