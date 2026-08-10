-- ==========================================
-- NovaBank - Base de datos: novabank_usuarios (auth-service)
-- Seed sanitizado de demo. Sin datos personales reales.
-- Se ejecuta solo en el primer arranque del volumen de PostgreSQL.
-- ==========================================

\connect postgres

CREATE DATABASE novabank_usuarios;

\connect novabank_usuarios

CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- Cuenta de administrador de demo: admin@admin.com / admin
INSERT INTO usuarios (email, password)
VALUES ('admin@admin.com', '$2a$10$1CLSiE37VYoMfd648bYL8eZ2G2YGg/l4N5a9iM7ppgSwD9OI3Dx6e');