package com.jlanzasg.novabank;

import com.jlanzasg.novabank.repository.ClienteRepository;
import com.jlanzasg.novabank.repository.Impl.ClienteRepositoryImpl;
import com.jlanzasg.novabank.repository.CuentaRepository;
import com.jlanzasg.novabank.repository.Impl.CuentaRepositoryImpl;
import com.jlanzasg.novabank.repository.MovimientoRepository;
import com.jlanzasg.novabank.repository.Impl.MovimientoRepositoryImpl;

import com.jlanzasg.novabank.service.ClienteService;
import com.jlanzasg.novabank.service.Impl.ClienteServiceImpl;
import com.jlanzasg.novabank.service.CuentaService;
import com.jlanzasg.novabank.service.Impl.CuentaServiceImpl;
import com.jlanzasg.novabank.service.OperacionService;
import com.jlanzasg.novabank.service.Impl.OperacionServiceImpl;
import com.jlanzasg.novabank.service.MovimientoService;
import com.jlanzasg.novabank.service.Impl.MovimientoServiceImpl;

import com.jlanzasg.novabank.view.*;

public class Aplicacion {
    public static void main(String[] args) {

        // REPOSITORIOS
        ClienteRepository clienteRepo = new ClienteRepositoryImpl();
        CuentaRepository cuentaRepo = new CuentaRepositoryImpl();
        MovimientoRepository movimientoRepo = new MovimientoRepositoryImpl();

        // SERVICIOS
        ClienteService clienteService = new ClienteServiceImpl(clienteRepo);
        CuentaService cuentaService = new CuentaServiceImpl(cuentaRepo, clienteService);
        OperacionService operacionService = new OperacionServiceImpl(cuentaRepo, movimientoRepo);
        MovimientoService movimientoService = new MovimientoServiceImpl(movimientoRepo, cuentaRepo);

        // VISTAS
        MenuCliente menuCliente = new MenuCliente(clienteService);
        MenuCuentas menuCuentas = new MenuCuentas(cuentaService);
        MenuOperaciones menuOperaciones = new MenuOperaciones(operacionService);
        MenuConsultas menuConsultas = new MenuConsultas(cuentaService, movimientoService);

        // MENU PRINCIPAL
        Menu menuPrincipal = new Menu(menuCliente, menuCuentas, menuOperaciones, menuConsultas);
        menuPrincipal.menuPrincipal();
    }
}