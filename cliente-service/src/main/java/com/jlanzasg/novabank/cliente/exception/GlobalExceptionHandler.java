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
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

/**
 * The type Global exception handler.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle duplicate exception mono.
     *
     * @param ex      the ex
     * @param request the request
     * @return the mono
     */
    @ExceptionHandler(DuplicateException.class)
    public Mono<ResponseEntity<ErrorResponseDTO>> handleDuplicateException(DuplicateException ex, ServerHttpRequest request) {
        return buildError(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request);
    }

    /**
     * Handle saldo insuficiente exception mono.
     *
     * @param ex      the ex
     * @param request the request
     * @return the mono
     */
    @ExceptionHandler(SaldoInsuficienteException.class)
    public Mono<ResponseEntity<ErrorResponseDTO>> handleSaldoInsuficienteException(SaldoInsuficienteException ex, ServerHttpRequest request) {
        return buildError(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request);
    }

    /**
     * Handle not found exception mono.
     *
     * @param ex      the ex
     * @param request the request
     * @return the mono
     */
    @ExceptionHandler(NotFoundException.class)
    public Mono<ResponseEntity<ErrorResponseDTO>> handleNotFoundException(NotFoundException ex, ServerHttpRequest request) {
        return buildError(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request);
    }

    /**
     * Handle validation exception mono.
     *
     * @param ex      the ex
     * @param request the request
     * @return the mono
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponseDTO>> handleValidationException(WebExchangeBindException ex, ServerHttpRequest request) {
        String mensajes = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(". "));
        return buildError(HttpStatus.BAD_REQUEST, "Bad Request", mensajes, request);
    }

    /**
     * Handle server web input exception mono.
     *
     * @param ex      the ex
     * @param request the request
     * @return the mono
     */
    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<ErrorResponseDTO>> handleServerWebInputException(ServerWebInputException ex, ServerHttpRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, "Bad Request", "Error en la solicitud: " + ex.getReason(), request);
    }

    /**
     * Handle response status exception mono.
     *
     * @param ex      the ex
     * @param request the request
     * @return the mono
     */
    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<ErrorResponseDTO>> handleResponseStatusException(ResponseStatusException ex, ServerHttpRequest request) {
        return buildError(HttpStatus.valueOf(ex.getStatusCode().value()), ex.getStatusCode().toString(), ex.getReason() != null ? ex.getReason() : "Error en la petición", request);
    }

    /**
     * Handle web client exception mono.
     *
     * @param ex      the ex
     * @param request the request
     * @return the mono
     */
    @ExceptionHandler(WebClientResponseException.class)
    public Mono<ResponseEntity<ErrorResponseDTO>> handleWebClientException(WebClientResponseException ex, ServerHttpRequest request) {
        return buildError(HttpStatus.valueOf(ex.getStatusCode().value()), "WebClient Error", "Error al comunicarse con otro microservicio: " + ex.getStatusText(), request);
    }

    /**
     * Handle service exception mono.
     *
     * @param ex      the ex
     * @param request the request
     * @return the mono
     */
    @ExceptionHandler(ServiceException.class)
    public Mono<ResponseEntity<ErrorResponseDTO>> handleServiceException(ServiceException ex, ServerHttpRequest request) {
        return buildError(HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable", ex.getMessage(), request);
    }

    /**
     * Handle access denied exception mono.
     *
     * @param ex      the ex
     * @param request the request
     * @return the mono
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public Mono<ResponseEntity<ErrorResponseDTO>> handleAccessDeniedException(org.springframework.security.access.AccessDeniedException ex, ServerHttpRequest request) {
        return buildError(HttpStatus.FORBIDDEN, "Forbidden", "No tienes permisos para acceder a este recurso.", request);
    }

    /**
     * Handle authentication exception mono.
     *
     * @param ex      the ex
     * @param request the request
     * @return the mono
     */
    @ExceptionHandler(AuthenticationException.class)
    public Mono<ResponseEntity<ErrorResponseDTO>> handleAuthenticationException(AuthenticationException ex, ServerHttpRequest request) {
        return buildError(HttpStatus.UNAUTHORIZED, "Unauthorized", "Credenciales incorrectas o autenticación inválida.", request);
    }

    /**
     * Handle global exception mono.
     *
     * @param ex      the ex
     * @param request the request
     * @return the mono
     */
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponseDTO>> handleGlobalException(Exception ex, ServerHttpRequest request) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Ocurrió un error inesperado en el servidor: " + ex.getMessage(), request);
    }

    /**
     * Construye la respuesta de error estándar.
     *
     * @param status
     * @param error
     * @param message
     * @param request
     * @return
     */
    private Mono<ResponseEntity<ErrorResponseDTO>> buildError(HttpStatus status, String error, String message, ServerHttpRequest request) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .status(status.value())
                .error(error)
                .message(message)
                .path(request.getPath().value())
                .build();
        return Mono.just(new ResponseEntity<>(errorResponse, status));
    }
}