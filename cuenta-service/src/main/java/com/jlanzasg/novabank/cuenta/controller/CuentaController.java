package com.jlanzasg.novabank.cuenta.controller;

import com.jlanzasg.novabank.cuenta.dto.cuenta.response.CuentaResponseDTO;
import com.jlanzasg.novabank.cuenta.dto.cuenta.response.CuentaSimpleResponseDTO;
import com.jlanzasg.novabank.cuenta.service.CuentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * The type Cuenta controller.
 */
@Tag(name = "Cuentas", description = "Endpoints para gestionar cuentas en el sistema de NovaBank")
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
     * Crear cuenta response entity.
     *
     * @param clienteId the cliente id
     * @return the response entity
     */
    @Operation(summary = "Crear una nueva cuenta para un cliente", description = "Genera una nueva cuenta bancaria asociada a un cliente existente utilizando su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta creada con éxito"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida, clienteId faltante o incorrecto"),
            @ApiResponse(responseCode = "404", description = "No se encontró un cliente con el ID proporcionado")
    })
    @PostMapping
    public ResponseEntity<CuentaResponseDTO> crearCuenta(@RequestParam Long clienteId) {
        return ResponseEntity.ok(cuentaService.crearCuenta(clienteId, null));
    }

    /**
     * Find by cliente id response entity.
     *
     * @param idCliente the id cliente
     * @return the response entity
     */
    @Operation(summary = "Obtener cuentas por ID de cliente", description = "Recupera una lista de cuentas bancarias asociadas a un cliente específico utilizando su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuentas recuperadas con éxito"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida, idCliente faltante o incorrecto"),
            @ApiResponse(responseCode = "404", description = "No se encontró un cliente con el ID proporcionado o el cliente no tiene cuentas asociadas")
    })
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<Set<CuentaSimpleResponseDTO>> findByClienteId(@PathVariable Long idCliente) {
        Set<CuentaSimpleResponseDTO> cuentas = cuentaService.findAccountsByClientId(idCliente);
        return ResponseEntity.ok(cuentas);
    }

    /**
     * Find by iban response entity.
     *
     * @param iban the iban
     * @return the response entity
     */
    @Operation(summary = "Obtener cuenta por IBAN", description = "Recupera los detalles de una cuenta bancaria utilizando su número IBAN único")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta recuperada con éxito"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida, formato de IBAN incorrecto o IBAN faltante"),
            @ApiResponse(responseCode = "404", description = "No se encontró una cuenta con el IBAN proporcionado")
    })
    @GetMapping("/iban/{iban}")
    public ResponseEntity<CuentaResponseDTO> findByIban(@PathVariable String iban) {
        CuentaResponseDTO cuenta = cuentaService.findAccountByIban(iban);
        return ResponseEntity.ok(cuenta);
    }

    /**
     * Actualizar saldo response entity.
     *
     * @param iban       the iban
     * @param nuevoSaldo the nuevo saldo
     * @return the response entity
     */
    @Operation(summary = "Actualizar saldo de la cuenta", description = "Actualiza el saldo de una cuenta bancaria (Endpoint interno para microservicios)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saldo actualizado con éxito"),
            @ApiResponse(responseCode = "404", description = "No se encontró una cuenta con el IBAN proporcionado")
    })
    @PutMapping("/iban/{iban}/saldo")
    public ResponseEntity<Void> actualizarSaldo(@PathVariable String iban, @RequestParam Double nuevoSaldo) {
        cuentaService.actualizarSaldo(iban, nuevoSaldo);
        return ResponseEntity.ok().build();
    }
}
