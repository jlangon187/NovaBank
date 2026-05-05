package com.jlanzasg.novabank.auth.controller;

import com.jlanzasg.novabank.auth.dto.auth.AuthRequestDTO;
import com.jlanzasg.novabank.auth.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth") // <--- El prefijo exacto del PDF
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    // Añade tu UsuarioRepository o UserService si tienes lógica de registro

    public AuthController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    // 1. POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        UserDetails user = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(token);
    }

    // 2. POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Object registerRequest) { // Cambia Object por tu DTO de registro
        // Aquí llamas a tu repositorio para guardar el usuario usando el passwordEncoder
        return ResponseEntity.ok("Usuario registrado con éxito");
    }

    // 3. GET /api/auth/validate?token=xxx
    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestParam String token) {
        boolean isValid = jwtService.isTokenValid(token);
        if (isValid) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(401).body(false);
        }
    }
}