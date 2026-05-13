package com.jlanzasg.novabank.auth.controller;

import com.jlanzasg.novabank.auth.dto.auth.request.AuthRequestDTO;
import com.jlanzasg.novabank.auth.dto.auth.response.AuthResponseDTO;
import com.jlanzasg.novabank.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * The type Auth controller.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * Instantiates a new Auth controller.
     *
     * @param authService the auth service
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Login mono.
     *
     * @param requestMono the request mono
     * @return the mono
     */
    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponseDTO>> login(@RequestBody Mono<AuthRequestDTO> requestMono) {
        return requestMono.flatMap(request -> authService.login(request))
                .map(token -> ResponseEntity.ok(new AuthResponseDTO(token)))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    /**
     * Validate token mono.
     *
     * @param token the token
     * @return the mono
     */
    @GetMapping("/validate")
    public Mono<ResponseEntity<Boolean>> validateToken(@RequestParam String token) {
        return authService.validateToken(token)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.ok(false)); // Si hay error validando, devuelve false
    }
}