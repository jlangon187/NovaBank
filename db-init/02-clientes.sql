-- ==========================================
-- NovaBank - Base de datos: novabank_clientes (cliente-service)
-- Seed sanitizado de demo. Sin datos personales reales.
-- Se ejecuta solo en el primer arranque del volumen de PostgreSQL.
-- ==========================================

\connect postgres

CREATE DATABASE novabank_clientes;

\connect novabank_clientes

CREATE TABLE clientes (
    id BIGSERIAL PRIMARY KEY,
    dni VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(150) NOT NULL,
    email VARCHAR(100),
    telefono VARCHAR(20),
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Cliente de demo ficticio
INSERT INTO clientes (dni, nombre, apellidos, email, telefono)
VALUES ('12345678Z', 'Cliente', 'Demo NovaBank', 'cliente@novabank.com', '600000000');