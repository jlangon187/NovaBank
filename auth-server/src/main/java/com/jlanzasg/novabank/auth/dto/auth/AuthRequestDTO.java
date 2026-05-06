package com.jlanzasg.novabank.auth.dto.auth;
import lombok.Data;

/**
 * The type Auth request dto.
 */
@Data
public class AuthRequestDTO {
    private String email;
    private String password;
}