package com.jlanzasg.novabank.controller;

import com.jlanzasg.novabank.dto.auth.AuthRequestDTO;
import com.jlanzasg.novabank.model.Usuario;
import com.jlanzasg.novabank.repository.UsuarioRepository;
import com.jlanzasg.novabank.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Autenticación", description = "Endpoints para iniciar sesión y generar usuarios de prueba")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(summary = "Iniciar sesión", description = "Recibe email y contraseña, y devuelve un token JWT" +
            " si las credenciales son correctas.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso, devuelve el token JWT."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Credenciales incorrectas.")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO request) {
        // 1. Spring Security comprueba que el email y la contraseña coinciden
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 2. Si es correcto, buscamos al usuario y le generamos el token
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail()).orElseThrow();
        String jwtToken = jwtService.generateToken(usuario);

        // 3. Devolvemos el token en formato JSON
        return ResponseEntity.ok(Map.of("token", jwtToken));
    }

    @Operation(summary = "Registrar usuario de prueba", description = "Crea un nuevo usuario con el email y contraseña" +
            " proporcionados. La contraseña se encripta automáticamente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente."),
            @ApiResponse(responseCode = "400", description = "Error al registrar el usuario.")
    })
    @PostMapping("/registro")
    public ResponseEntity<?> registrarPrueba(@RequestBody AuthRequestDTO request) {
        Usuario nuevoUser = Usuario.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // ENCRIPTAMOS
                .build();
        usuarioRepository.save(nuevoUser);
        return ResponseEntity.ok("Usuario guardado con éxito. Ya puedes hacer login.");
    }
}