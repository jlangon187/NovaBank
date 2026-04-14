package com.jlanzasg.novabank.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * The type Database connection manager.
 */
public class DatabaseConnectionManager {
    private static final String URL = "jdbc:postgresql://localhost:5432/novabank_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "root";

    private static DatabaseConnectionManager instance;

    private DatabaseConnectionManager() {}

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static DatabaseConnectionManager getInstance() {
        if (instance == null) {
            instance = new DatabaseConnectionManager();
        }
        return instance;
    }

    /**
     * Gets conexion.
     *
     * @return the conexion
     * @throws SQLException the sql exception
     */
    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}