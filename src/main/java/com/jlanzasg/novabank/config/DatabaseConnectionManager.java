package com.jlanzasg.novabank.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * The type Conexion db.
 */
public class DatabaseConnectionManager {
    private static Connection conexion;
    private static final String URL = "jdbc:postgresql://localhost:5432/novabank_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "root";

    private DatabaseConnectionManager() {
        System.out.println("Iniciando conexion");
    }

    /**
     * Gets conexion.
     *
     * @return the conexion
     */
    public static Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Conexión establecida");
            }
        } catch (SQLException e) {
            System.out.println("Error al conectar con la base de datos");
            throw new RuntimeException("Fallo en la conexión: " + e.getMessage());
        }
        return conexion;
    }
}
