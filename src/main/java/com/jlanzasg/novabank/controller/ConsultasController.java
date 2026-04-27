package com.jlanzasg.novabank.controller;

import com.jlanzasg.novabank.dto.operacion.response.MovimientoResponseDTO;
import com.jlanzasg.novabank.service.ConsultaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * The type Consultas controller.
 */
@Tag(name = "Consultas", description = "Endpoints para gestionar consultas en el sistema de NovaBank")
@RestController
@RequestMapping("/consultas")
public class ConsultasController {

    private final ConsultaService consultaService;

    /**
     * Instantiates a new Consultas controller.
     *
     * @param consultaService the consulta service
     */
    public ConsultasController(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    /**
     * Obtener movimientos response entity.
     *
     * @param id          the id
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
    @GetMapping("/{id}/movimientos")
    public ResponseEntity<List<MovimientoResponseDTO>> obtenerMovimientos(
            @PathVariable Long id,
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

        List<MovimientoResponseDTO> movimientos = consultaService.obtenerMovimientosPorCuentaYFecha(id, inicio, fin);
        return ResponseEntity.ok(movimientos);
    }
}