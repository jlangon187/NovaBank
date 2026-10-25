-- ==========================================
-- NovaBank - Base de datos: novabank_cuentas (cuenta-service)
-- Seed sanitizado de demo. Sin datos personales reales.
-- Se ejecuta solo en el primer arranque del volumen de PostgreSQL.
-- ==========================================

\connect postgres

CREATE DATABASE novabank_cuentas;

\connect novabank_cuentas

CREATE TABLE cuentas (
    id BIGSERIAL PRIMARY KEY,
    iban VARCHAR(34) NOT NULL UNIQUE,
    balance DOUBLE PRECISION DEFAULT 0.0,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    cliente_id BIGINT NOT NULL,
    version BIGINT DEFAULT 0
);