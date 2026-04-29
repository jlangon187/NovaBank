package com.jlanzasg.novabank.dto.cliente.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * The type Cliente response dto.
 */
@Data
public class ClienteResponseDTO {
    private Long id;
    private String dni;
    private String nombre;
    private String apellidos;
    private String email;
    private String telefono;
    private LocalDateTime fecha;
}
