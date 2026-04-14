package com.jlanzasg.novabank;

import com.jlanzasg.novabank.repository.ClienteRepository;
import com.jlanzasg.novabank.repository.Impl.ClienteRepositoryImpl;
import com.jlanzasg.novabank.repository.CuentaRepository;
import com.jlanzasg.novabank.repository.Impl.CuentaRepositoryImpl;
import com.jlanzasg.novabank.service.ClienteService;
import com.jlanzasg.novabank.service.Impl.ClienteServiceImpl;
import com.jlanzasg.novabank.service.CuentaService;
import com.jlanzasg.novabank.service.Impl.CuentaServiceImpl;
import com.jlanzasg.novabank.view.*;


public class Aplicacion {
    public static void main(String[] args) {

        ClienteRepository clienteRepo = new ClienteRepositoryImpl();
        CuentaRepository cuentaRepo = new CuentaRepositoryImpl();
        ClienteService clienteService = new ClienteServiceImpl(clienteRepo);
        CuentaService cuentaService = new CuentaServiceImpl(cuentaRepo, clienteService);

        MenuCliente menuCliente = new MenuCliente(clienteService);
        MenuCuentas menuCuentas = new MenuCuentas(cuentaService);
        MenuOperaciones menuOperaciones = new MenuOperaciones();
        MenuConsultas menuConsultas = new MenuConsultas();

        Menu menuPrincipal = new Menu(menuCliente, menuCuentas, menuOperaciones, menuConsultas);
        menuPrincipal.menuPrincipal();
    }
}
