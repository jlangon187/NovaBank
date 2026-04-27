package com.jlanzasg.novabank.service;

import com.jlanzasg.novabank.dto.cuenta.response.CuentaResponseDTO;
import com.jlanzasg.novabank.exception.NotFoundException;
import com.jlanzasg.novabank.mapper.impl.CuentaMapper;
import com.jlanzasg.novabank.model.Cliente;
import com.jlanzasg.novabank.model.Cuenta;
import com.jlanzasg.novabank.repository.ClienteRepository;
import com.jlanzasg.novabank.repository.CuentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * The type Cuenta service.
 */
@Service
public class CuentaService {

    private final CuentaRepository cuentaRepository;
    private final ClienteRepository clienteRepository;
    private final CuentaMapper cuentaMapper;

    /**
     * Instantiates a new Cuenta service.
     *
     * @param cuentaRepository  the cuenta repository
     * @param clienteRepository the cliente repository
     * @param cuentaMapper      the cuenta mapper
     */
    public CuentaService(CuentaRepository cuentaRepository, ClienteRepository clienteRepository, CuentaMapper cuentaMapper) {
        this.cuentaRepository = cuentaRepository;
        this.clienteRepository = clienteRepository;
        this.cuentaMapper = cuentaMapper;
    }

    /**
     * Crear cuenta cuenta response dto.
     *
     * @param clienteId the request dto
     * @return the cuenta response dto
     */
    @Transactional
    public CuentaResponseDTO crearCuenta(Long clienteId) {

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new NotFoundException("No se encontró el cliente con ID: " + clienteId));

        String nuevoIban = generarIban();

        Cuenta nuevaCuenta = Cuenta.builder()
                .iban(nuevoIban)
                .balance(0.0) // El balance inicial se establece en 0.0
                .build();

        cliente.addCuenta(nuevaCuenta);

        Cuenta cuentaGuardada = cuentaRepository.save(nuevaCuenta);

        return cuentaMapper.toResponseDTO(cuentaGuardada);
    }

    /**
     * Find accounts by client id list.
     *
     * @param idCliente the id cliente
     * @return the list
     */
    public List<CuentaResponseDTO> findAccountsByClientId(Long idCliente) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new NotFoundException("No se encontró el cliente con ID: " + idCliente));
        return cuentaMapper.toResponseDTOList(cliente.getCuentas());
    }

    /**
     * Find account by iban cuenta response dto.
     *
     * @param iban the iban
     * @return the cuenta response dto
     */
    public CuentaResponseDTO findAccountByIban(String iban) {
        Cuenta cuenta = cuentaRepository.findByIban(iban)
                .orElseThrow(() -> new NotFoundException("No se encontró la cuenta con IBAN: " + iban));
        return cuentaMapper.toResponseDTO(cuenta);
    }

    /**
     * Generar iban string.
     *
     * @return the string
     */
    public String generarIban() {
        Long ultimoID = cuentaRepository.obtenerUltimoId().orElse(0L);

        StringBuilder sb = new StringBuilder();
        String prefijo = "ES91210000";
        String numeroSecuencial = String.format("%012d", ++ultimoID);
        sb.append(prefijo);
        sb.append(numeroSecuencial);

        return sb.toString();
    }

}
