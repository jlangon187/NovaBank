package com.jlanzasg.novabank.auth.dto.error;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * The type Error response dto.
 */
@Data
@Builder
public class ErrorResponseDTO {

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    private int status;
    private String error;
    private String message;
    private String path;
}