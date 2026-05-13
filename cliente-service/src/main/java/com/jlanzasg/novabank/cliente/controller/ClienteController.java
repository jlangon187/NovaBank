package com.jlanzasg.novabank.cliente.controller;

import com.jlanzasg.novabank.cliente.dto.cliente.request.ClienteRequestDTO;
import com.jlanzasg.novabank.cliente.dto.cliente.response.ClienteResponseDTO;
import com.jlanzasg.novabank.cliente.service.ClienteService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The type Cliente controller.
 */
@Tag(name = "Clientes", description = "Endpoints para gestionar clientes en el sistema de NovaBank")
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
     * Save cliente mono.
     *
     * @param clienteRequestDTO the cliente request dto
     * @return the mono
     */
    @Operation(summary = "Crear un nuevo cliente", description = "Agrega un nuevo cliente a la base de datos con los datos proporcionados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente creado con éxito"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida, datos incorrectos o faltantes"),
            @ApiResponse(responseCode = "409", description = "Conflicto, el DNI, email o teléfono ya existe en la base de datos")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ClienteResponseDTO> saveCliente(@Valid @RequestBody ClienteRequestDTO clienteRequestDTO) {
        return clienteService.save(clienteRequestDTO);
    }

    /**
     * Find by id mono.
     *
     * @param id the id
     * @return the mono
     */
    @Operation(summary = "Buscar cliente por ID", description = "Recupera un cliente de la base de datos utilizando su ID único")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado con éxito")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ClienteResponseDTO> findById(@PathVariable Long id) {
        return clienteService.findById(id);
    }

    /**
     * Find by dni mono.
     *
     * @param dni the dni
     * @return the mono
     */
    @Operation(summary = "Buscar cliente por DNI", description = "Busca un cliente en la base de datos utilizando su DNI único")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado con éxito")
    @GetMapping("/dni/{dni}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ClienteResponseDTO> findByDni(@PathVariable String dni) {
        return clienteService.findByDni(dni);
    }

    /**
     * Listar clientes flux.
     *
     * @return the flux
     */
    @Operation(summary = "Listar todos los clientes", description = "Retorna una lista de todos los clientes registrados")
    @ApiResponse(responseCode = "200", description = "Lista obtenida con éxito")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<ClienteResponseDTO> listarClientes() {
        return clienteService.findAll();
    }

    /**
     * Delete cliente mono.
     *
     * @param id the id
     * @return the mono
     */
    @Operation(summary = "Eliminar un cliente", description = "Borra un cliente de la base de datos mediante su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteCliente(@PathVariable Long id) {
        return clienteService.deleteById(id);
    }
}