package com.jlanzasg.novabank.service;

import com.jlanzasg.novabank.model.Cliente;
import com.jlanzasg.novabank.repository.ClienteRepository;
import com.jlanzasg.novabank.utils.Validacion;

import java.util.List;
import java.util.Optional;

/**
 * The type Cliente service.
 */
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository repositorio;

    public ClienteServiceImpl(ClienteRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public void registrarCliente(Cliente cliente) {

        if (!Validacion.esNombreValido(cliente.getNombre())) {
            throw new IllegalArgumentException("El nombre debe tener entre 2 y 50 caracteres (sin números).");
        }
        if (!Validacion.esNombreValido(cliente.getApellido())) {
            throw new IllegalArgumentException("El apellido debe tener entre 2 y 50 caracteres (sin números).");
        }
        if (!Validacion.esDniValido(cliente.getDni())) {
            throw new IllegalArgumentException("El formato del DNI es incorrecto (8 números y 1 letra).");
        }
        if (!Validacion.esEmailValido(cliente.getEmail())) {
            throw new IllegalArgumentException("El formato del email es incorrecto.");
        }
        if (!Validacion.esTelefonoValido(cliente.getTelefono())) {
            throw new IllegalArgumentException("El formato del teléfono es incorrecto (debe tener 9 dígitos).");
        }
        if (repositorio.buscarPorDni(cliente.getDni()).isPresent()) {
            throw new IllegalStateException("Ya existe un cliente registrado con el DNI " + cliente.getDni());
        }

        repositorio.guardar(cliente);
    }

    @Override
    public Optional<Cliente> buscarClientePorId(Long id) {

        if (id == null || id <= 0 ) {
            throw new IllegalArgumentException("La ID del cliente debe ser un número positivo.");
        }

        Optional<Cliente> cliente = repositorio.buscarPorId(id);

        if (cliente.isEmpty()) {
            throw new IllegalArgumentException("No se ha encontrado el cliente con ID: " + id);
        }

        return cliente;
    }

    @Override
    public Optional<Cliente> buscarClientePorDni(String dni) {
        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("El DNI no puede estar vacío.");
        }
        if (!Validacion.esDniValido(dni)) {
            throw new IllegalArgumentException("El formato del DNI es incorrecto (8 números y 1 letra).");
        }

        Optional<Cliente> cliente = repositorio.buscarPorDni(dni);

        if (cliente.isEmpty()) {
            throw new IllegalArgumentException("No se encontro el cliente con DNI: " + dni);
        }
        return cliente;
    }

    @Override
    public List<Cliente> listarClientes() {
        return repositorio.listarTodos();
    }

    @Override
    public void eliminarCliente(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("La ID del cliente debe ser un número positivo.");
        }
        repositorio.eliminar(id);
    }
}
