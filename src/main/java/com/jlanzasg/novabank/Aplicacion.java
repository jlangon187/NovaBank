package com.jlanzasg.novabank;

import com.jlanzasg.novabank.service.Banco;
import com.jlanzasg.novabank.view.Menu;
import com.jlanzasg.novabank.config.DatabaseConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Aplicacion {
    public static void main(String[] args) {

        // Inicializo las clases
        Banco banco = new Banco();
        Menu menu = new Menu();

        try (Statement stmt = DatabaseConnectionManager.getConexion().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM cuentas")) {
            if (rs.next()) {
                System.out.println(rs.getString("numero_cuenta"));
            } else {
                System.out.println("No hay registros en la cuenta");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        // Menu principal
        menu.menuPrincipal(banco);
    }
}
