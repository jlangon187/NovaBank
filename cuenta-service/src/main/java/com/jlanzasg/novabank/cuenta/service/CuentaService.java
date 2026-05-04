package com.jlanzasg.novabank.cuenta.service;

import com.jlanzasg.novabank.cuenta.client.ClienteClient;
import com.jlanzasg.novabank.cuenta.dto.cliente.response.ClienteResponseDTO;
import com.jlanzasg.novabank.cuenta.dto.cuenta.request.CuentaRequestDTO;
import com.jlanzasg.novabank.cuenta.dto.cuenta.response.CuentaResponseDTO;
import com.jlanzasg.novabank.cuenta.dto.cuenta.response.CuentaSimpleResponseDTO;
import com.jlanzasg.novabank.cuenta.exception.NotFoundException;
import com.jlanzasg.novabank.cuenta.exception.ServiceException;
import com.jlanzasg.novabank.cuenta.mapper.impl.CuentaMapper;
import com.jlanzasg.novabank.cuenta.model.Cuenta;
import com.jlanzasg.novabank.cuenta.repository.CuentaRepository;
import feign.FeignException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * The type Cuenta service.
 */
@Service
public class CuentaService {

    private final CuentaRepository cuentaRepository;
    private final ClienteClient clienteClient;
    private final CuentaMapper cuentaMapper;

    /**
     * Instantiates a new Cuenta service.
     *
     * @param cuentaRepository the cuenta repository
     * @param clienteClient    the cliente client
     * @param cuentaMapper     the cuenta mapper
     */
    public CuentaService(CuentaRepository cuentaRepository, ClienteClient clienteClient, CuentaMapper cuentaMapper) {
        this.cuentaRepository = cuentaRepository;
        this.clienteClient = clienteClient;
        this.cuentaMapper = cuentaMapper;
    }

    /**
     * Crear cuenta cuenta response dto.
     *
     * @param clienteId        the request dto
     * @param cuentaRequestDTO the cuenta request dto
     * @return the cuenta response dto
     */
    @Transactional
    public CuentaResponseDTO crearCuenta(Long clienteId, CuentaRequestDTO cuentaRequestDTO) {

        ClienteResponseDTO clienteDTO;

        try {
            clienteDTO = clienteClient.getClienteById(clienteId);
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("No se encontró el cliente con ID: " + clienteId);
        }

        String nuevoIban = generarIban();

        Cuenta nuevaCuenta = Cuenta.builder()
                .iban(nuevoIban)
                .balance(0.0) // El balance inicial se establece en 0.0
                .clienteId(clienteId)
                .build();

        Cuenta cuentaGuardada = cuentaRepository.save(nuevaCuenta);

        return cuentaMapper.toResponseDTO(cuentaGuardada, clienteDTO);
    }

    /**
     * Find accounts by client id list.
     *
     * @param clienteId the id cliente
     * @return the list
     */
    public Set<CuentaSimpleResponseDTO> findAccountsByClientId(Long clienteId) {
        try {
            clienteClient.getClienteById(clienteId);
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("No se encontró el cliente con ID: " + clienteId);
        }
        return cuentaMapper.toSimpleResponseDTOList(cuentaRepository.findAllByClienteId(clienteId));
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

        ClienteResponseDTO clienteDTO;
        try {
            clienteDTO = clienteClient.getClienteById(cuenta.getClienteId());
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("No se encontró el cliente con ID: " + cuenta.getClienteId());
        } catch (FeignException e) {
            throw new ServiceException("El servicio de clientes falló al buscar el ID: " + cuenta.getClienteId());
        }

        return cuentaMapper.toResponseDTO(cuenta, clienteDTO);
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

    /**
     * Actualizar saldo.
     *
     * @param iban       the iban
     * @param nuevoSaldo the nuevo saldo
     */
    @Transactional
    public void actualizarSaldo(String iban, Double nuevoSaldo) {
        Cuenta cuenta = cuentaRepository.findByIban(iban)
                .orElseThrow(() -> new NotFoundException("La cuenta con IBAN " + iban + " no existe."));

        cuenta.setBalance(nuevoSaldo);
        cuentaRepository.save(cuenta);
    }
}
