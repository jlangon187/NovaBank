package com.jlanzasg.novabank.dto.cliente.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * The type Cliente request dto.
 */
@Data
public class ClienteRequestDTO {

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "^[0-9]{8}[A-Z]$", message = "El DNI tiene que tener 8 números y una letra al final")
    private String dni;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 20, message = "El nombre debe tener entre 3 y 20 caracteres")
    private String nombre;

    @NotBlank(message = "Los apellidos es obligatorio")
    private String apellidos;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un formato de email válido")
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9]{9}$", message = "El teléfono debe tener 9 dígitos")
    private String telefono;
}