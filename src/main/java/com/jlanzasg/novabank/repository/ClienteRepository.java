package com.jlanzasg.novabank.repository;

import com.jlanzasg.novabank.model.Cliente;

import java.util.List;
import java.util.Optional;

/**
 * The interface Cliente repository.
 */
public interface ClienteRepository {

    /**
     * Guardar cliente.
     *
     * @param cliente the cliente
     * @return the cliente
     */
    Cliente guardar(Cliente cliente);

    /**
     * Buscar por id optional.
     *
     * @param id the id
     * @return the optional
     */
    Optional<Cliente> buscarPorId(Long id);

    /**
     * Buscar por dni optional.
     *
     * @param dni the dni
     * @return the optional
     */
    Optional<Cliente> buscarPorDni(String dni);

    /**
     * Listar todos list.
     *
     * @return the list
     */
    List<Cliente> listarTodos();

    /**
     * Eliminar.
     *
     * @param id the id
     */
    void eliminar(Long id);
}
