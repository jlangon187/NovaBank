package com.jlanzasg.novabank.operacion.service;

import com.jlanzasg.novabank.operacion.client.ExchangeRateClient;
import com.jlanzasg.novabank.operacion.dto.cuenta.request.ActualizarSaldosRequestDTO;
import com.jlanzasg.novabank.operacion.dto.cuenta.CuentaResponseDTO;
import com.jlanzasg.novabank.operacion.dto.cuenta.CuentaSaldoResponseDTO;
import com.jlanzasg.novabank.operacion.dto.operacion.request.OperacionRequestDTO;
import com.jlanzasg.novabank.operacion.dto.operacion.request.TransferenciaRequestDTO;
import com.jlanzasg.novabank.operacion.dto.operacion.response.MovimientoResponseDTO;
import com.jlanzasg.novabank.operacion.exception.DuplicateException;
import com.jlanzasg.novabank.operacion.exception.ExchangeRateUnavailableException;
import com.jlanzasg.novabank.operacion.exception.NotFoundException;
import com.jlanzasg.novabank.operacion.exception.SaldoInsuficienteException;
import com.jlanzasg.novabank.operacion.mapper.impl.OperacionMapper;
import com.jlanzasg.novabank.operacion.model.Movimiento;
import com.jlanzasg.novabank.operacion.model.TipoMovimiento;
import com.jlanzasg.novabank.operacion.repository.OperacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * The type Operacion service.
 */
@Service
public class OperacionService {

    private final OperacionRepository operacionRepository;
    private final WebClient cuentaWebClient;
    private final OperacionMapper operacionMapper;
    private final ExchangeRateClient exchangeRateClient;
    /**
     * Instantiates a new Operacion service.
     *
     * @param operacionRepository the operacion repository
     * @param webClientBuilder    the web client builder
     * @param operacionMapper     the operacion mapper
     */
    @Autowired
    public OperacionService(OperacionRepository operacionRepository, WebClient.Builder webClientBuilder, OperacionMapper operacionMapper, ExchangeRateClient exchangeRateClient) {
        this.operacionRepository = operacionRepository;
        this.cuentaWebClient = webClientBuilder.baseUrl("http://cuenta-service").build();
        this.operacionMapper = operacionMapper;
        this.exchangeRateClient = exchangeRateClient;
    }

    public OperacionService(OperacionRepository operacionRepository, WebClient.Builder webClientBuilder, ExchangeRateClient exchangeRateClient, OperacionMapper operacionMapper, String cuentaServiceBaseUrl) {
        this.operacionRepository = operacionRepository;
        this.cuentaWebClient = webClientBuilder.baseUrl(cuentaServiceBaseUrl).build();
        this.operacionMapper = operacionMapper;
        this.exchangeRateClient = exchangeRateClient;
    }

    /**
     * Depositar mono.
     *
     * @param dto the dto
     * @return the mono
     */
    @Transactional
    public Mono<MovimientoResponseDTO> depositar(OperacionRequestDTO dto) {

        if (dto.getImporte() <= 0) {
            return Mono.error(new IllegalArgumentException("El importe del depósito debe ser mayor que cero."));
        }

        return obtenerCuenta(dto.getCuentaIban())
                .flatMap(cuentaDTO -> {
                    Double nuevoSaldo = cuentaDTO.getBalance() + dto.getImporte();

                    return actualizarSaldoUnico(cuentaDTO.getIban(), nuevoSaldo)
                            .then(guardarMovimiento(dto.getCuentaIban(), dto.getImporte(), TipoMovimiento.DEPOSITO));
                });
    }

    /**
     * Retirar mono.
     *
     * @param dto the dto
     * @return the mono
     */
    @Transactional
    public Mono<MovimientoResponseDTO> retirar(OperacionRequestDTO dto) {

        if (dto.getImporte() <= 0) {
            return Mono.error(new IllegalArgumentException("El importe de la retirada debe ser mayor que cero."));
        }

        return obtenerCuenta(dto.getCuentaIban())
                .flatMap(cuentaDTO -> {
                    if (cuentaDTO.getBalance() < dto.getImporte()) {
                        return Mono.error(new SaldoInsuficienteException("Fondos insuficientes. Balance actual: " + cuentaDTO.getBalance()));
                    }

                    Double nuevoSaldo = cuentaDTO.getBalance() - dto.getImporte();

                    return actualizarSaldoUnico(cuentaDTO.getIban(), nuevoSaldo)
                            .then(guardarMovimiento(dto.getCuentaIban(), dto.getImporte(), TipoMovimiento.RETIRO));
                });
    }

    /**
     * Transferir flux.
     *
     * @param dto the dto
     * @return the flux
     */
    @Transactional
    public Flux<MovimientoResponseDTO> transferir(TransferenciaRequestDTO dto) {
        if (dto.getImporte() <= 0) {
            return Flux.error(new IllegalArgumentException("El importe de la transferencia debe ser mayor que cero."));
        }
        if (dto.getCuentaOrigen().equals(dto.getCuentaDestino())) {
            return Flux.error(new DuplicateException("No se puede realizar una transferencia a la misma cuenta."));
        }

        return Mono.zip(
                obtenerCuenta(dto.getCuentaOrigen()),
                obtenerCuenta(dto.getCuentaDestino()),
                obtenerTasaCambioSegura(dto.getMonedaOrigen(), dto.getMonedaDestino())
        ).flatMapMany(tuple -> {
            CuentaResponseDTO cuentaOrigen = tuple.getT1();
            CuentaResponseDTO cuentaDestino = tuple.getT2();
            Double tasa = tuple.getT3();

            if (cuentaOrigen.getBalance() < dto.getImporte()) {
                return Flux.error(new SaldoInsuficienteException("Fondos insuficientes en la cuenta de origen. Balance actual: " + cuentaOrigen.getBalance()));
            }


            Double importeExtraido = dto.getImporte();
            Double importeIngresado = dto.getImporte() * tasa;

            ActualizarSaldosRequestDTO actualizarSaldosRequest = new ActualizarSaldosRequestDTO();
            actualizarSaldosRequest.setIbanOrigen(dto.getCuentaOrigen());
            actualizarSaldosRequest.setIbanDestino(dto.getCuentaDestino());
            actualizarSaldosRequest.setNuevoSaldoOrigen(cuentaOrigen.getBalance() - importeExtraido);
            actualizarSaldosRequest.setNuevoSaldoDestino(cuentaDestino.getBalance() + importeIngresado);

            return cuentaWebClient.put()
                    .uri("/cuentas/saldos")
                    .bodyValue(actualizarSaldosRequest)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .thenMany(Flux.concat(
                            guardarMovimiento(dto.getCuentaOrigen(), importeExtraido, TipoMovimiento.TRANSFERENCIA_SALIENTE),
                            guardarMovimiento(dto.getCuentaDestino(), importeIngresado, TipoMovimiento.TRANSFERENCIA_ENTRANTE)
                    ));
        });
    }

    /**
     * Obtener movimientos por cuenta y fecha flux.
     *
     * @param iban        the iban
     * @param fechaInicio the fecha inicio
     * @param fechaFin    the fecha fin
     * @return the flux
     */
    @Transactional(readOnly = true)
    public Flux<MovimientoResponseDTO> obtenerMovimientosPorCuentaYFecha(String iban, LocalDateTime
            fechaInicio, LocalDateTime fechaFin) {

        Flux<Movimiento> movimiento;

        if (fechaInicio != null && fechaFin != null) {
            movimiento = operacionRepository.findByIbanAndFechaBetweenOrderByFechaDesc(iban, fechaInicio, fechaFin);
        } else {
            movimiento = operacionRepository.findByIbanOrderByFechaDesc(iban);
        }

        return movimiento.map(operacionMapper::toResponseDTO);
    }

    /**
     * Consultar saldo mono.
     *
     * @param iban the iban
     * @return the mono
     */
    public Mono<CuentaSaldoResponseDTO> consultarSaldo(String iban) {
        return cuentaWebClient.get()
                .uri("/cuentas/iban/{iban}", iban)
                .retrieve()
                .bodyToMono(CuentaSaldoResponseDTO.class)
                .onErrorResume(WebClientResponseException.NotFound.class,
                        e -> Mono.error(new NotFoundException("La cuenta con IBAN " + iban + " no existe.")));
    }

    /**
     * Actualizar saldo unico mono.
     *
     * @param iban
     * @param nuevoSaldo
     * @return
     */
    private Mono<Void> actualizarSaldoUnico(String iban, Double nuevoSaldo) {
        return cuentaWebClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/cuentas/iban/{iban}/saldo")
                        .queryParam("nuevoSaldo", nuevoSaldo)
                        .build(iban))
                .retrieve()
                .bodyToMono(Void.class);
    }

    /**
     * Obtener la cuenta mono.
     *
     * @param iban
     * @return
     */
    private Mono<CuentaResponseDTO> obtenerCuenta(String iban) {
        return cuentaWebClient.get()
                .uri("/cuentas/iban/{iban}", iban)
                .retrieve()
                .bodyToMono(CuentaResponseDTO.class)
                .onErrorResume(WebClientResponseException.NotFound.class,
                        e -> Mono.error(new NotFoundException("La cuenta con IBAN " + iban + " no existe.")));
    }

    /**
     * Guardar movimiento mono.
     *
     * @param iban
     * @param importe
     * @param tipoMovimiento
     * @return
     */
    private Mono<MovimientoResponseDTO> guardarMovimiento(String iban, Double importe, TipoMovimiento tipoMovimiento) {
        Movimiento movimiento = Movimiento.builder()
                .tipo(tipoMovimiento)
                .cantidad(importe)
                .iban(iban)
                .fecha(LocalDateTime.now())
                .build();

        return operacionRepository.save(movimiento)
                .map(operacionMapper::toResponseDTO)
                .flatMap(this::publicarMovimientoCuentaService);
    }

    /**
     * Obtener tasa cambio segura mono.
     *
     * @param monedaOrigen
     * @return
     */
    private Mono<Double> obtenerTasaCambioSegura(String monedaOrigen, String monedaDestino) {
        String from = (monedaOrigen == null || monedaOrigen.isBlank()) ? "EUR" : monedaOrigen;
        String to = (monedaDestino == null || monedaDestino.isBlank()) ? "EUR" : monedaDestino;

        if (from.equalsIgnoreCase(to)) {
            return Mono.just(1.0);
        }

        return exchangeRateClient.obtenerTasaCambioSegura(from, to)
                .onErrorMap(ex -> ex instanceof ExchangeRateUnavailableException
                        ? ex
                        : new ExchangeRateUnavailableException(from, to, ex));
    }

    private Mono<MovimientoResponseDTO> publicarMovimientoCuentaService(MovimientoResponseDTO movimientoResponseDTO) {
        return cuentaWebClient.post()
                .uri("/cuentas/movimientos/evento")
                .bodyValue(movimientoResponseDTO)
                .retrieve()
                .bodyToMono(Void.class)
                .thenReturn(movimientoResponseDTO);
    }

    public Flux<MovimientoResponseDTO> obtenerStreamingMovimientos() {
        return Flux.empty();
    }
}
