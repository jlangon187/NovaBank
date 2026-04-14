package com.jlanzasg.novabank.service.Impl;

import com.jlanzasg.novabank.model.Cliente;
import com.jlanzasg.novabank.model.Cuenta;
import com.jlanzasg.novabank.repository.CuentaRepository;
import com.jlanzasg.novabank.service.ClienteService;
import com.jlanzasg.novabank.service.CuentaService;
import com.jlanzasg.novabank.utils.Validacion;

import java.util.List;
import java.util.Optional;

/**
 * The type Cuenta service.
 */
public class CuentaServiceImpl implements CuentaService {

    private final CuentaRepository repositorio;
    private final ClienteService clienteService;

    public CuentaServiceImpl(CuentaRepository repositorio, ClienteService clienteService) {
        this.repositorio = repositorio;
        this.clienteService = clienteService;
    }

    @Override
    public Cuenta crearCuenta(Long clienteId) {
        if (clienteId == null || clienteId <= 0) {
            throw new IllegalArgumentException("El numero de cliente debe ser un numero positivo");
        }
        if (clienteService.buscarClientePorId(clienteId).isEmpty()) {
            throw new IllegalArgumentException("El cliente no existe");
        }

        Cliente cliente = Cliente.builder().id(clienteId).build();

        Cuenta cuenta = Cuenta.builder().cliente(cliente).iban(generarIban()).build();
        return repositorio.guardar(cuenta);
    }

    @Override
    public List<Cuenta> consultarCuentasDeCliente(Long clienteId) {
        if (clienteId == null || clienteId <= 0) {
            throw new IllegalArgumentException("El numero de cliente debe ser un numero positivo");
        }
        if (clienteService.buscarClientePorId(clienteId).isEmpty()) {
            throw new IllegalArgumentException("El cliente no existe");
        }
        return repositorio.buscarPorClienteId(clienteId);
    }

    @Override
    public Optional<Cuenta> consultarCuenta(String iban) {
        if (!Validacion.esIbanValido(iban)) {
            throw new IllegalArgumentException("El formato del IBAN es incorrecto.");
        }
        return repositorio.buscarPorNumero(iban);
    }

    /**
     * Generar iban string.
     * @return
     */
    public String generarIban() {
        Long ultimoID = repositorio.obtenerUltimoId();

        StringBuilder sb = new StringBuilder();
        String prefijo = "ES91210000";
        String numeroSecuencial = String.format("%012d", ++ultimoID);
        sb.append(prefijo);
        sb.append(numeroSecuencial);

        return sb.toString();
    }
}
