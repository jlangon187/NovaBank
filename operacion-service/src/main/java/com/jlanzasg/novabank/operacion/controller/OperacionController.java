package com.jlanzasg.novabank.operacion.controller;

import com.jlanzasg.novabank.operacion.dto.operacion.request.OperacionRequestDTO;
import com.jlanzasg.novabank.operacion.dto.cuenta.response.CuentaSaldoResponseDTO;
import com.jlanzasg.novabank.operacion.dto.operacion.request.TransferenciaRequestDTO;
import com.jlanzasg.novabank.operacion.dto.operacion.response.MovimientoResponseDTO;
import com.jlanzasg.novabank.operacion.service.OperacionService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Tag(name = "Operaciones", description = "Endpoints para gestionar operaciones en el sistema de NovaBank")
@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(
                title = "API NovaBank",
                version = "1.0",
                description = "API REST del sistema de NovaBank"
        ))
@RestController
@RequestMapping("/operaciones")
public class OperacionController {

    private final OperacionService operacionService;

    public OperacionController(OperacionService operacionService) {
        this.operacionService = operacionService;
    }

    @Operation(summary = "Realizar un depósito en una cuenta", description = "Permite realizar un depósito en una cuenta bancaria" +
            " especificando el importe y la cuenta de destino")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Depósito realizado con éxito"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida, datos incorrectos o faltantes"),
            @ApiResponse(responseCode = "404", description = "No se encontró una cuenta con el IBAN proporcionado")
    })
    @PostMapping("/deposito")
    public ResponseEntity<MovimientoResponseDTO> deposito(@Valid @RequestBody OperacionRequestDTO operacionRequest) {
        return ResponseEntity.ok(operacionService.depositar(operacionRequest));
    }

    @Operation(summary = "Realizar un retiro en una cuenta", description = "Permite realizar un retiro en una cuenta bancaria" +
            " especificando el importe y la cuenta de destino")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retiro realizado con éxito"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida, datos incorrectos o faltantes"),
            @ApiResponse(responseCode = "404", description = "No se encontró una cuenta con el IBAN proporcionado")
    })
    @PostMapping("/retiro")
    public ResponseEntity<MovimientoResponseDTO> retiro(@Valid @RequestBody OperacionRequestDTO operacionRequest) {
        return ResponseEntity.ok(operacionService.retirar(operacionRequest));
    }

    @Operation(summary = "Realizar una transferencia entre cuentas", description = "Permite realizar una transferencia" +
            " entre dos cuentas bancarias especificando el importe, la cuenta de origen y la cuenta de destino")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transferencia realizada con éxito"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida, datos incorrectos o faltantes"),
            @ApiResponse(responseCode = "404", description = "No se encontró una cuenta con el IBAN de origen o destino proporcionado"),
            @ApiResponse(responseCode = "409", description = "Conflicto, saldo insuficiente en la cuenta de origen para realizar la transferencia")
    })
    @PostMapping("/transferencia")
    public ResponseEntity<List<MovimientoResponseDTO>> transferencia(@Valid @RequestBody TransferenciaRequestDTO transferenciaRequest) {
        return ResponseEntity.ok(operacionService.transferir(transferenciaRequest));
    }

    /**
     * Obtener movimientos response entity.
     *
     * @param iban         the iban
     * @param fechaInicio the fecha inicio
     * @param fechaFin    the fecha fin
     * @return the response entity
     */
    @Operation(summary = "Obtener movimientos de una cuenta", description = "Permite obtener los movimientos" +
            " de una cuenta específica, con la opción de filtrar por rango de fechas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimientos obtenidos exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada"),
            @ApiResponse(responseCode = "400", description = "Parámetros de fecha inválidos")
    })
    @GetMapping("/movimientos/{iban}")
    public ResponseEntity<List<MovimientoResponseDTO>> obtenerMovimientos(
            @PathVariable String iban,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        LocalDateTime inicio = null;
        LocalDateTime fin = null;

        if (fechaInicio != null) {
            inicio = fechaInicio.atStartOfDay();
        }

        if (fechaFin != null) {
            fin = fechaFin.atTime(LocalTime.MAX);
        }

        List<MovimientoResponseDTO> movimientos = operacionService.obtenerMovimientosPorCuentaYFecha(iban, inicio, fin);
        return ResponseEntity.ok(movimientos);
    }

    /**
     * Obtener saldo.
     * @param iban
     * @return
     */
    @Operation(summary = "Obtener el saldo de una cuenta", description = "Permite obtener el saldo de una cuenta a traves" +
            " del IBAN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saldo obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    @GetMapping("/saldo/{iban}")
    public ResponseEntity<CuentaSaldoResponseDTO> obtenerSaldo(@PathVariable String iban) {
        CuentaSaldoResponseDTO cuenta = operacionService.consultarSaldo(iban);
        return ResponseEntity.ok(cuenta);
    }
}
