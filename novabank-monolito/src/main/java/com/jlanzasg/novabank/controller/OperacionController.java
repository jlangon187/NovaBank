package com.jlanzasg.novabank.controller;

import com.jlanzasg.novabank.dto.operacion.request.OperacionRequestDTO;
import com.jlanzasg.novabank.dto.operacion.request.TransferenciaRequestDTO;
import com.jlanzasg.novabank.dto.operacion.response.MovimientoResponseDTO;
import com.jlanzasg.novabank.service.OperacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Operaciones", description = "Endpoints para gestionar operaciones en el sistema de NovaBank")
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
}
