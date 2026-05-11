package com.jlanzasg.novabank.cliente.exception;

import com.jlanzasg.novabank.cliente.dto.error.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;

/**
 * Clase para manejar excepciones globalmente en la aplicación. Utiliza @RestControllerAdvice para interceptar excepciones
 * lanzadas por los controladores y devolver respuestas HTTP adecuadas con mensajes de error personalizados.
 * Esto permite centralizar el manejo de errores y mantener la API consistente y fácil de depurar,
 * evitando que se expongan detalles internos de la aplicación al cliente.
 * Cada método maneja un tipo específico de excepción y devuelve un ErrorResponseDTO
 * con información relevante sobre el error ocurrido.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle duplicate exception response entity.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ErrorResponseDTO> handleDuplicateException(DuplicateException ex, ServerHttpRequest request) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .status(HttpStatus.CONFLICT.value())
                .error("Conflict")
                .message(ex.getMessage())
                .path(request.getPath().value())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }


    /**
     * Handle saldo insuficiente exception response entity.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<ErrorResponseDTO> handleSaldoInsuficienteException(SaldoInsuficienteException ex, ServerHttpRequest request) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .status(HttpStatus.CONFLICT.value())
                .error("Conflict")
                .message(ex.getMessage())
                .path(request.getPath().value())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Handle not found exception response entity.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFoundException(NotFoundException ex, ServerHttpRequest request) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(ex.getMessage())
                .path(request.getPath().value())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle validation exception response entity.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(WebExchangeBindException ex, ServerHttpRequest request) {
        StringBuilder mensajes = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                mensajes.append(error.getField()).append(": ").append(error.getDefaultMessage()).append(". ")
        );

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(mensajes.toString().trim())
                .path(request.getPath().value())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle server web input exception response entity.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<ErrorResponseDTO> handleServerWebInputException(ServerWebInputException ex, ServerHttpRequest request) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message("Error en la solicitud: " + ex.getReason())
                .path(request.getPath().value())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle response status exception response entity.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponseDTO> handleResponseStatusException(ResponseStatusException ex, ServerHttpRequest request) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .status(ex.getStatusCode().value())
                .error(ex.getStatusCode().toString())
                .message(ex.getReason() != null ? ex.getReason() : "Error en la petición")
                .path(request.getPath().value())
                .build();
        return new ResponseEntity<>(errorResponse, ex.getStatusCode());
    }

    /**
     * Handle web client exception response entity.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ErrorResponseDTO> handleWebClientException(WebClientResponseException ex, ServerHttpRequest request) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .status(ex.getStatusCode().value())
                .error("WebClient Error")
                .message("Error al comunicarse con otro microservicio: " + ex.getStatusText())
                .path(request.getPath().value())
                .build();
        return new ResponseEntity<>(errorResponse, ex.getStatusCode());
    }

    /**
     * Handle service exception response entity.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponseDTO> handleServiceException(ServiceException ex, ServerHttpRequest request) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .error("Service Unavailable")
                .message(ex.getMessage())
                .path(request.getPath().value())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * Handle access denied exception response entity.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDeniedException(org.springframework.security.access.AccessDeniedException ex, ServerHttpRequest request) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .error("Forbidden")
                .message("No tienes permisos para acceder a este recurso.")
                .path(request.getPath().value())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * Handle authentication exception response entity.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> handleAuthenticationException(AuthenticationException ex, ServerHttpRequest request) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Unauthorized")
                .message("Credenciales incorrectas o autenticación inválida.")
                .path(request.getPath().value())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handle global exception response entity.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGlobalException(Exception ex, ServerHttpRequest request) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("Ocurrió un error inesperado en el servidor: " + ex.getMessage())
                .path(request.getPath().value())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
