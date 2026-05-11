package com.jlanzasg.novabank.cuenta.service;

import com.jlanzasg.novabank.cuenta.dto.cliente.response.ClienteResponseDTO;
import com.jlanzasg.novabank.cuenta.dto.cuenta.request.CuentaRequestDTO;
import com.jlanzasg.novabank.cuenta.dto.cuenta.response.CuentaResponseDTO;
import com.jlanzasg.novabank.cuenta.dto.cuenta.response.CuentaSimpleResponseDTO;
import com.jlanzasg.novabank.cuenta.exception.NotFoundException;
import com.jlanzasg.novabank.cuenta.mapper.impl.CuentaMapper;
import com.jlanzasg.novabank.cuenta.model.Cuenta;
import com.jlanzasg.novabank.cuenta.repository.CuentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The type Cuenta service.
 */
@Service
public class CuentaService {

    private final CuentaRepository cuentaRepository;
    private final WebClient webClient;
    private final CuentaMapper cuentaMapper;


    /**
     * Instantiates a new Cuenta service.
     *
     * @param cuentaRepository the cuenta repository
     * @param webClientBuilder the web client builder
     * @param cuentaMapper     the cuenta mapper
     */
    public CuentaService(CuentaRepository cuentaRepository, WebClient.Builder webClientBuilder, CuentaMapper cuentaMapper) {
        this.cuentaRepository = cuentaRepository;
        this.webClient = webClientBuilder.baseUrl("http://cliente-service").build();
        this.cuentaMapper = cuentaMapper;
    }

    /**
     * Crear cuenta mono.
     *
     * @param clienteId        the cliente id
     * @param cuentaRequestDTO the cuenta request dto
     * @return the mono
     */
    @Transactional
    public Mono<CuentaResponseDTO> crearCuenta(Long clienteId, CuentaRequestDTO cuentaRequestDTO) {
        return obtenerCliente(clienteId)
                .flatMap(clienteDTO -> generarIban()
                        .flatMap(iban -> {
                            Cuenta cuenta = cuentaMapper.toEntity(cuentaRequestDTO);
                            cuenta.setClienteId(clienteId);
                            cuenta.setIban(iban);

                            return cuentaRepository.save(cuenta)
                                    .map(cuentaGuardada -> cuentaMapper.toResponseDTO(cuentaGuardada, clienteDTO));
                        })
                );
    }

    /**
     * Find accounts by client id flux.
     *
     * @param clienteId the cliente id
     * @return the flux
     */
    public Flux<CuentaSimpleResponseDTO> findAccountsByClientId(Long clienteId) {
        return cuentaRepository.findAllByClienteId(clienteId)
                .map(cuentaMapper::toSimpleResponseDTO);
    }

    /**
     * Find account by iban mono.
     *
     * @param iban the iban
     * @return the mono
     */
    public Mono<CuentaResponseDTO> findAccountByIban(String iban) {
        return cuentaRepository.findByIban(iban)
                .switchIfEmpty(Mono.error(new NotFoundException("No se encontró la cuenta con IBAN: " + iban)))
                .flatMap(cuenta -> obtenerCliente(cuenta.getClienteId())
                        .map(clienteDTO -> cuentaMapper.toResponseDTO(cuenta, clienteDTO))
                );
    }

    /**
     * Generar iban string.
     *
     * @return the string
     */
    public Mono<String> generarIban() {
        return cuentaRepository.obtenerUltimoId()
                .defaultIfEmpty(0L)
                .map(ultimo -> {
                    String prefijo = "ES91210000";
                    String numeroSecuencial = String.format("%012d", ultimo + 1);
                    return prefijo + numeroSecuencial;
                });
    }

    /**
     * Actualizar saldo mono.
     *
     * @param iban       the iban
     * @param nuevoSaldo the nuevo saldo
     * @return the mono
     */
    @Transactional
    public Mono<Void> actualizarSaldo(String iban, Double nuevoSaldo) {
        return cuentaRepository.findByIban(iban)
                .switchIfEmpty(Mono.error(new NotFoundException("No se encontró la cuenta con IBAN: " + iban)))
                .flatMap(cuenta -> {
                    cuenta.setBalance(nuevoSaldo);
                    return cuentaRepository.save(cuenta).then();
                });
    }

    /**
     * Obtener cliente mono.
     *
     * @param clienteId
     * @return
     */
    private Mono<ClienteResponseDTO> obtenerCliente(Long clienteId) {
        return webClient.get()
                .uri("/clientes/{id}", clienteId)
                .retrieve()
                .bodyToMono(ClienteResponseDTO.class)
                .onErrorResume(WebClientResponseException.NotFound.class,
                        e -> Mono.error(new NotFoundException("No se encontró el cliente con ID: " + clienteId)));
    }

    /**
     * Actualizar saldos mono.
     *
     * @param ibanOrigen
     * @param ibanDestino
     * @param nuevoSaldoOrigen
     * @param nuevoSaldoDestino
     * @return
     */
    @Transactional
    public Mono<Void> actualizarSaldos(String ibanOrigen, Double nuevoSaldoOrigen, String ibanDestino, Double nuevoSaldoDestino) {
        Mono<Cuenta> cuentaOrigenMono = cuentaRepository.findByIban(ibanOrigen)
                .switchIfEmpty(Mono.error(new NotFoundException("No se encontró la cuenta con IBAN: " + ibanOrigen)));
        Mono<Cuenta> cuentaDestinoMono = cuentaRepository.findByIban(ibanDestino)
                .switchIfEmpty(Mono.error(new NotFoundException("No se encontró la cuenta con IBAN: " + ibanDestino)));

        return Mono.zip(cuentaOrigenMono, cuentaDestinoMono)
                .flatMap(tuple -> {
                    Cuenta cuentaOrigen = tuple.getT1();
                    Cuenta cuentaDestino = tuple.getT2();

                    cuentaOrigen.setBalance(cuentaOrigen.getBalance() - nuevoSaldoOrigen);
                    cuentaDestino.setBalance(cuentaDestino.getBalance() + nuevoSaldoDestino);

                    return cuentaRepository.save(cuentaOrigen)
                            .then(cuentaRepository.save(cuentaDestino));
                }).then();
    }
}
