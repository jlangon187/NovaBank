-- ==========================================
-- NovaBank - Base de datos: novabank_operaciones (operacion-service)
-- Seed sanitizado de demo. Sin datos personales reales.
-- Se ejecuta solo en el primer arranque del volumen de PostgreSQL.
-- ==========================================

\connect postgres

CREATE DATABASE novabank_operaciones;

\connect novabank_operaciones

CREATE TABLE movimientos (
    id BIGSERIAL PRIMARY KEY,
    iban VARCHAR(34) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    cantidad DOUBLE PRECISION NOT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);