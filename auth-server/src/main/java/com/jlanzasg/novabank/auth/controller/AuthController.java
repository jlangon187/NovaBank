package com.jlanzasg.novabank.auth.controller;

import com.jlanzasg.novabank.auth.dto.auth.request.AuthRequestDTO;
import com.jlanzasg.novabank.auth.dto.auth.response.AuthResponseDTO;
import com.jlanzasg.novabank.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponseDTO>> login(@RequestBody Mono<AuthRequestDTO> requestMono) {
        return requestMono.flatMap(request -> authService.login(request))
                .map(token -> ResponseEntity.ok(new AuthResponseDTO(token)))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @GetMapping("/validate")
    public Mono<ResponseEntity<Boolean>> validateToken(@RequestParam String token) {
        return authService.validateToken(token)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.ok(false)); // Si hay error validando, devuelve false
    }
}