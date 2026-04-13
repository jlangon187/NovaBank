package com.jlanzasg.novabank;

import com.jlanzasg.novabank.repository.ClienteRepository;
import com.jlanzasg.novabank.repository.ClienteRepositoryJdbc;
import com.jlanzasg.novabank.service.ClienteService;
import com.jlanzasg.novabank.service.ClienteServiceImpl;
import com.jlanzasg.novabank.view.*;


public class Aplicacion {
    public static void main(String[] args) {

        ClienteRepository clienteRepo = new ClienteRepositoryJdbc();
        ClienteService clienteService = new ClienteServiceImpl(clienteRepo);

        MenuCliente menuCliente = new MenuCliente(clienteService);
        MenuCuentas menuCuentas = new MenuCuentas();
        MenuOperaciones menuOperaciones = new MenuOperaciones();
        MenuConsultas menuConsultas = new MenuConsultas();

        Menu menuPrincipal = new Menu(menuCliente, menuCuentas, menuOperaciones, menuConsultas);
        menuPrincipal.menuPrincipal();
    }
}
