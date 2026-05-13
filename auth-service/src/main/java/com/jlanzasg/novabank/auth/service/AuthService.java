package com.jlanzasg.novabank.auth.service;

import com.jlanzasg.novabank.auth.dto.auth.request.AuthRequestDTO;
import com.jlanzasg.novabank.auth.repository.UsuarioRepository;
import com.jlanzasg.novabank.auth.security.JwtProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * The type Auth service.
 */
@Service
public class AuthService {

    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    private final UsuarioRepository usuarioRepository;

    /**
     * Instantiates a new Auth service.
     *
     * @param jwtProvider       the jwt provider
     * @param passwordEncoder   the password encoder
     * @param usuarioRepository the usuario repository
     */
    public AuthService(JwtProvider jwtProvider, PasswordEncoder passwordEncoder, UsuarioRepository usuarioRepository) {
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Lógica de login reactiva
     *
     * @param request the request
     * @return the mono
     */
    public Mono<String> login(AuthRequestDTO request) {

        return usuarioRepository.findByEmail(request.getEmail())
                .filter(usuario -> passwordEncoder.matches(request.getPassword(), usuario.getPassword()))
                .map(usuario -> jwtProvider.generateToken(usuario.getEmail()));
    }

    /**
     * Valida si un token es correcto (Lo usa el API Gateway)
     *
     * @param token the token
     * @return the mono
     */
    public Mono<Boolean> validateToken(String token) {
        return Mono.fromCallable(() -> jwtProvider.validateToken(token));
    }
}