package com.jlanzasg.novabank;

import com.jlanzasg.novabank.negocio.Banco;
import com.jlanzasg.novabank.vista.Menu;

public class Aplicacion {
    public static void main(String[] args) {

        // Inicializo las clases
        Banco banco = new Banco();
        Menu menu = new Menu();

        // Menu principal
        menu.menuPrincipal(banco);
    }
}
