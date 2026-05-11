package com.jlanzasg.novabank.cliente.service;

import com.jlanzasg.novabank.cliente.dto.cliente.request.ClienteRequestDTO;
import com.jlanzasg.novabank.cliente.dto.cliente.response.ClienteResponseDTO;
import com.jlanzasg.novabank.cliente.exception.DuplicateException;
import com.jlanzasg.novabank.cliente.exception.NotFoundException;
import com.jlanzasg.novabank.cliente.mapper.impl.ClienteMapper;
import com.jlanzasg.novabank.cliente.model.Cliente;
import com.jlanzasg.novabank.cliente.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * The type Cliente service.
 */
@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    /**
     * Instantiates a new Cliente service.
     *
     * @param clienteRepository the cliente repository
     * @param clienteMapper     the cliente mapper
     */
    public ClienteService(ClienteRepository clienteRepository, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    /**
     * Save mono.
     *
     * @param clienteDto the cliente dto
     * @return the mono
     */
    public Mono<ClienteResponseDTO> save(ClienteRequestDTO clienteDto) {

        return clienteRepository.findConflictos(
                        clienteDto.getDni(),
                        clienteDto.getEmail(),
                        clienteDto.getTelefono())
                .collectList()
                .flatMap(conflictos -> {

                    if (!conflictos.isEmpty()) {
                        String error = conflictos.get(0);
                        return switch (error) {
                            case "DNI" ->
                                    Mono.error(new DuplicateException("El DNI " + clienteDto.getDni() + " ya existe en la base de datos"));
                            case "EMAIL" ->
                                    Mono.error(new DuplicateException("El email " + clienteDto.getEmail() + " ya existe en la base de datos"));
                            case "TELEFONO" ->
                                    Mono.error(new DuplicateException("El teléfono " + clienteDto.getTelefono() + " ya existe en la base de datos"));
                            default -> Mono.error(new RuntimeException("Conflicto desconocido"));
                        };
                    }

                    Cliente clienteMapeado = clienteMapper.toEntity(clienteDto);
                    return clienteRepository.save(clienteMapeado)
                            .map(clienteMapper::toResponseDTO);
                });
    }

    /**
     * Find by id mono.
     *
     * @param id the id
     * @return the mono
     */
    public Mono<ClienteResponseDTO> findById(Long id) {
        return clienteRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Cliente con id " + id + " no encontrado")))
                .map(clienteMapper::toResponseDTO);
    }

    /**
     * Find by dni mono.
     *
     * @param dni the dni
     * @return the mono
     */
    public Mono<ClienteResponseDTO> findByDni(String dni) {
        return clienteRepository.findByDni(dni.toUpperCase())
                .switchIfEmpty(Mono.error(new NotFoundException("Cliente con DNI " + dni.toUpperCase() + " no encontrado")))
                .map(clienteMapper::toResponseDTO);
    }

    /**
     * Find all flux.
     *
     * @return the flux
     */
    public Flux<ClienteResponseDTO> findAll() {
        return clienteRepository.findAll()
                .map(clienteMapper::toResponseDTO);
    }

    /**
     * Delete by id mono.
     *
     * @param id the id
     * @return the mono
     */
    public Mono<Void> deleteById(Long id) {
        return clienteRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Cliente con id " + id + " no encontrado")))
                .flatMap(cliente -> clienteRepository.delete(cliente));
    }
}
