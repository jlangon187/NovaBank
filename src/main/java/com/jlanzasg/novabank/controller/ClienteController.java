package com.jlanzasg.novabank.controller;

import com.jlanzasg.novabank.dto.cliente.request.ClienteRequestDTO;
import com.jlanzasg.novabank.dto.cliente.response.ClienteResponseDTO;
import com.jlanzasg.novabank.service.ClienteService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The type Cliente controller.
 */
@Tag(name = "Clientes", description = "Endpoints para gestionar clientes en el sistema de NovaBank")
@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(
                title = "API NovaBank",
                version = "1.0",
                description = "API REST del sistema de NovaBank"
        ))
@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    /**
     * Instantiates a new Cliente controller.
     *
     * @param clienteService the cliente service
     */
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    /**
     * Save cliente cliente response dto.
     *
     * @param clienteRequestDTO the cliente request dto
     * @return the cliente response dto
     */
    @Operation(summary = "Crear un nuevo cliente", description = "Agrega un nuevo cliente a la base de datos con los datos proporcionados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente creado con éxito"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida, datos incorrectos o faltantes"),
            @ApiResponse(responseCode = "409", description = "Conflicto, el DNI, email o teléfono ya existe en la base de datos")
    })
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> saveCliente(@Valid @RequestBody ClienteRequestDTO clienteRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.save(clienteRequestDTO));
    }

    /**
     * Find by id cliente response dto.
     *
     * @param id the id
     * @return the cliente response dto
     */
    @Operation(summary = "Buscar cliente por ID", description = "Recupera un cliente de la base de datos utilizando su ID único")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado con éxito")
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.findById(id));
    }

    /**
     * Find by dni cliente response dto.
     *
     * @param dni the dni
     * @return the cliente response dto
     */
    @Operation(summary = "Buscar cliente por DNI", description = "Busca un cliente en la base de datos utilizando su DNI único")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado con éxito")
    @GetMapping("/dni/{dni}")
    public ResponseEntity<ClienteResponseDTO> findByDni(@PathVariable String dni) {
        return ResponseEntity.ok(clienteService.findByDni(dni));
    }

    /**
     * Listar clientes response entity.
     *
     * @return the response entity
     */
    @Operation(summary = "Listar todos los clientes", description = "Retorna una lista de todos los clientes registrados")
    @ApiResponse(responseCode = "200", description = "Lista obtenida con éxito")
    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarClientes() {
        List<ClienteResponseDTO> clientes = clienteService.findAll();
        return ResponseEntity.ok(clientes);
    }

    /**
     * Delete cliente response entity.
     *
     * @param id the id
     * @return the response entity
     */
    @Operation(summary = "Eliminar un cliente", description = "Borra un cliente de la base de datos mediante su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        clienteService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}