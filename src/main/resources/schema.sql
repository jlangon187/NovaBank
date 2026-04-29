CREATE DATABASE novabank_db;

CREATE TABLE clientes (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(150) NOT NULL,
    dni VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    telefono VARCHAR(20) UNIQUE NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_email_valido CHECK (email ~* '^[A-Za-z0-9._+%-]+@[A-Za-z0-9.-]+\.[A-Za-z]+$')
);

CREATE TABLE cuentas (
    id SERIAL PRIMARY KEY,
    numero_cuenta VARCHAR(34) UNIQUE NOT NULL,
    cliente_id INT NOT NULL,
    saldo NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE,
    CONSTRAINT chk_saldo_positivo CHECK (saldo >= 0)
);

CREATE TABLE movimientos (
    id SERIAL PRIMARY KEY,
    cuenta_id INT NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    cantidad NUMERIC(15,2) NOT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cuenta FOREIGN KEY (cuenta_id) REFERENCES cuentas(id) ON DELETE CASCADE,
    CONSTRAINT chk_tipo_movimiento CHECK (tipo IN ('DEPOSITO', 'RETIRO', 'TRANSFERENCIA_SALIENTE', 'TRANSFERENCIA_ENTRANTE')),
    CONSTRAINT chk_cantidad_positiva CHECK (cantidad > 0)
);

CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    email    VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

--   Usuario de prueba
--  "email": "admin@admin.com",
--  "password": "admin1234"
--