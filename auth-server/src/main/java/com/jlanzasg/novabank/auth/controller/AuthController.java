package com.jlanzasg.novabank.auth.controller;

import com.jlanzasg.novabank.auth.dto.auth.AuthRequestDTO;
import com.jlanzasg.novabank.auth.repository.UsuarioRepository;
import com.jlanzasg.novabank.auth.security.JwtService;
import com.jlanzasg.novabank.auth.model.Usuario;
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

/**
 * The type Auth controller.
 */
@Tag(name = "Autenticación", description = "Endpoints para iniciar sesión y generar usuarios de prueba")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Instantiates a new Auth controller.
     *
     * @param authenticationManager the authentication manager
     * @param jwtService            the jwt service
     * @param usuarioRepository     the usuario repository
     * @param passwordEncoder       the password encoder
     */
    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Login response entity.
     *
     * @param request the request
     * @return the response entity
     */
    @Operation(summary = "Iniciar sesión", description = "Recibe email y contraseña, y devuelve un token JWT en formato JSON.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso, devuelve el token JWT."),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas.")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail()).orElseThrow();
        String jwtToken = jwtService.generateToken(usuario);

        return ResponseEntity.ok(Map.of("token", jwtToken));
    }

    /**
     * Registrar prueba response entity.
     *
     * @param request the request
     * @return the response entity
     */
    @Operation(summary = "Registrar usuario", description = "Crea un nuevo usuario encriptando su contraseña.")
    @PostMapping("/register") // 2. SOLUCIONADO: Nombre exacto que pide el PDF
    public ResponseEntity<?> registrarPrueba(@RequestBody AuthRequestDTO request) {
        Usuario nuevoUser = Usuario.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        usuarioRepository.save(nuevoUser);
        return ResponseEntity.ok(Map.of("message", "Usuario guardado con éxito. Ya puedes hacer login."));
    }

    /**
     * Validate token response entity.
     *
     * @param token the token
     * @return the response entity
     */
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