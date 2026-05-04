package com.jlanzasg.novabank.cliente.service;

import com.jlanzasg.novabank.cliente.dto.cliente.request.ClienteRequestDTO;
import com.jlanzasg.novabank.cliente.dto.cliente.response.ClienteResponseDTO;
import com.jlanzasg.novabank.cliente.exception.DuplicateException;
import com.jlanzasg.novabank.cliente.exception.NotFoundException;
import com.jlanzasg.novabank.cliente.mapper.impl.ClienteMapper;
import com.jlanzasg.novabank.cliente.model.Cliente;
import com.jlanzasg.novabank.cliente.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
     * Save cliente response dto.
     *
     * @param clienteDto the cliente dto
     * @return the cliente response dto
     */
    public ClienteResponseDTO save(ClienteRequestDTO clienteDto) {

        List<String> conflictos = clienteRepository.findConflictos(
                clienteDto.getDni(),
                clienteDto.getEmail(),
                clienteDto.getTelefono());

        if (!conflictos.isEmpty()) {
            String error = conflictos.get(0);
            switch (error) {
                case "DNI" -> throw new DuplicateException("El DNI " + clienteDto.getDni() + " ya existe en la base de datos");
                case "EMAIL" -> throw new DuplicateException("El email " + clienteDto.getEmail() + " ya existe en la base de datos");
                case "TELEFONO" -> throw new DuplicateException("El teléfono " + clienteDto.getTelefono() + " ya existe en la base de datos");
            }
        }

        Cliente clienteMapeado = clienteMapper.toEntity(clienteDto);
        clienteRepository.save(clienteMapeado);
        return clienteMapper.toResponseDTO(clienteMapeado);
    }

    /**
     * Find by ID Cliente.
     *
     * @param id the id
     * @return ClienteResponseDTO
     */
    public ClienteResponseDTO findById(Long id) {
        Cliente clienteMapeado = clienteRepository.findById(id).orElseThrow(() -> new NotFoundException("No se ha encontrado el cliente con el id: " + id));
        return clienteMapper.toResponseDTO(clienteMapeado);
    }

    /**
     * Find by DNI Cliente
     *
     * @param dni
     * @return ClienteResponseDTO
     */
    public ClienteResponseDTO findByDni(String dni) {
        Cliente clienteMapeado = clienteRepository.findByDni(dni).orElseThrow(() -> new NotFoundException("Cliente con DNI " + dni + " no encontrado"));
        return clienteMapper.toResponseDTO(clienteMapeado);
    }

    /**
     * Find all list.
     *
     * @return the list
     */
    public List<ClienteResponseDTO> findAll() {
        List<Cliente> clientes = clienteRepository.findAll();

        return clientes.stream()
                .map(clienteMapper::toResponseDTO)
                .toList();
    }

    /**
     * Delete by id.
     *
     * @param id the id
     */
    public void deleteById(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new NotFoundException("Cliente con id " + id + " no encontrado");
        }
        clienteRepository.deleteById(id);
    }
}
