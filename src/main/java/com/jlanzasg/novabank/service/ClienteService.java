package com.jlanzasg.novabank.service;

import com.jlanzasg.novabank.model.Cliente;

import java.util.List;
import java.util.Optional;

/**
 * The interface Cliente service.
 */
public interface ClienteService {

    /**
     * Registrar cliente.
     *
     * @param cliente the cliente
     */
    void registrarCliente(Cliente cliente);

    /**
     * Buscar cliente por id cliente.
     *
     * @param id the id
     * @return the cliente
     */
    Optional<Cliente> buscarClientePorId(Long id);

    /**
     * Buscar cliente por dni cliente.
     *
     * @param dni the dni
     * @return the cliente
     */
    Optional<Cliente> buscarClientePorDni(String dni);

    /**
     * Listar clientes list.
     *
     * @return the list
     */
    List<Cliente> listarClientes();

    /**
     * Eliminar cliente.
     *
     * @param id the id
     */
    void eliminarCliente(Long id);
}
