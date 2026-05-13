package com.jlanzasg.novabank.cuenta.controller;

import com.jlanzasg.novabank.cuenta.dto.cuenta.request.ActualizarSaldosRequestDTO;
import com.jlanzasg.novabank.cuenta.dto.cuenta.request.CuentaRequestDTO;
import com.jlanzasg.novabank.cuenta.dto.cuenta.response.CuentaResponseDTO;
import com.jlanzasg.novabank.cuenta.dto.cuenta.response.CuentaSimpleResponseDTO;
import com.jlanzasg.novabank.cuenta.dto.movimiento.response.MovimientoResponseDTO;
import com.jlanzasg.novabank.cuenta.service.CuentaService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The type Cuenta controller.
 */
@Tag(name = "Cuentas", description = "Endpoints para gestionar cuentas en el sistema de NovaBank")
@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(
                title = "API NovaBank",
                version = "1.0",
                description = "API REST del sistema de NovaBank"
        ))
@RestController
@RequestMapping("/cuentas")
public class CuentaController {

    private final CuentaService cuentaService;

    /**
     * Instantiates a new Cuenta controller.
     *
     * @param cuentaService the cuenta service
     */
    public CuentaController(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    /**
     * Crear cuenta mono.
     *
     * @param clienteId the cliente id
     * @return the mono
     */
    @Operation(summary = "Crear una nueva cuenta para un cliente", description = "Genera una nueva cuenta bancaria asociada a un cliente existente utilizando su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta creada con éxito"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida, clienteId faltante o incorrecto"),
            @ApiResponse(responseCode = "404", description = "No se encontró un cliente con el ID proporcionado")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CuentaResponseDTO> crearCuenta(@RequestParam Long clienteId) {
        return cuentaService.crearCuenta(clienteId, new CuentaRequestDTO());
    }

    /**
     * Find by cliente id flux.
     *
     * @param idCliente the id cliente
     * @return the flux
     */
    @Operation(summary = "Obtener cuentas por ID de cliente", description = "Recupera una lista de cuentas bancarias asociadas a un cliente específico utilizando su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuentas recuperadas con éxito"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida, idCliente faltante o incorrecto"),
            @ApiResponse(responseCode = "404", description = "No se encontró un cliente con el ID proporcionado o el cliente no tiene cuentas asociadas")
    })
    @GetMapping("/cliente/{idCliente}")
    @ResponseStatus(HttpStatus.OK)
    public Flux<CuentaSimpleResponseDTO> findByClienteId(@PathVariable Long idCliente) {
        return cuentaService.findAccountsByClientId(idCliente);
    }

    /**
     * Find by iban mono.
     *
     * @param iban the iban
     * @return the mono
     */
    @Operation(summary = "Obtener cuenta por IBAN", description = "Recupera los detalles de una cuenta bancaria utilizando su número IBAN único")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta recuperada con éxito"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida, formato de IBAN incorrecto o IBAN faltante"),
            @ApiResponse(responseCode = "404", description = "No se encontró una cuenta con el IBAN proporcionado")
    })
    @GetMapping("/iban/{iban}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<CuentaResponseDTO> findByIban(@PathVariable String iban) {
        return cuentaService.findAccountByIban(iban);
    }

    /**
     * Actualizar saldo mono.
     *
     * @param iban       the iban
     * @param nuevoSaldo the nuevo saldo
     * @return the mono
     */
    @Operation(summary = "Actualizar saldo de la cuenta", description = "Actualiza el saldo de una cuenta bancaria (Endpoint interno para microservicios)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saldo actualizado con éxito"),
            @ApiResponse(responseCode = "404", description = "No se encontró una cuenta con el IBAN proporcionado")
    })
    @PutMapping("/iban/{iban}/saldo")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> actualizarSaldo(@PathVariable String iban, @RequestParam Double nuevoSaldo) {
        return cuentaService.actualizarSaldo(iban, nuevoSaldo);
    }


    /**
     * Actualizar saldo mono.
     *
     * @param request the request
     * @return the mono
     */
    @Operation(summary = "Actualizar saldos de cuentas (Transferencia)", description = "Actualiza los saldos" +
            " de dos cuentas bancarias involucradas en una transferencia (Endpoint interno para microservicios)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saldos actualizados con éxito"),
            @ApiResponse(responseCode = "404", description = "No se encontró una cuenta con el IBAN proporcionado para alguna de las cuentas involucradas")
    })
    @PutMapping("/saldos")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> actualizarSaldo(@RequestBody ActualizarSaldosRequestDTO request) {
        return cuentaService.actualizarSaldos(request);
    }

    @GetMapping(value = "/{id}/movimientos/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Flux<MovimientoResponseDTO> streamMovimientosPorCuenta(@PathVariable("id") Long idCuenta) {
        return cuentaService.streamMovimientosByCuenta(idCuenta);
    }

    @PostMapping("/movimientos/evento")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Void> publicarMovimiento(@RequestBody MovimientoResponseDTO movimientoResponseDTO) {
        cuentaService.emitirMovimiento(movimientoResponseDTO);
        return Mono.empty();
    }
}
