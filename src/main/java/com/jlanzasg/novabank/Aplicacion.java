package com.jlanzasg.novabank;

import com.jlanzasg.novabank.negocio.Banco;
import com.jlanzasg.novabank.vista.Menu;

public class Aplicacion {
    public static void main(String[] args) {

        // Inicializo el banco
        Banco banco = new Banco();

        // Menu principal
        Menu.menuPrincipal(banco);
    }
}
