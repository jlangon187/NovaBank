package com.jlanzasg.novabank.auth.config;

import com.jlanzasg.novabank.auth.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * The type Application config.
 */
@Configuration
public class ApplicationConfig {

    private final UsuarioRepository usuarioRepository;

    /**
     * Instantiates a new Application config.
     *
     * @param usuarioRepository the usuario repository
     */
    public ApplicationConfig(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * User details service user details service.
     *
     * @return the user details service
     */
// 1. Cómo buscar al usuario en la base de datos
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    /**
     * Authentication provider authentication provider.
     *
     * @return the authentication provider
     */
// 2. El proveedor que junta el UserDetailsService con el encriptador de contraseñas
    @Bean
    public AuthenticationProvider authenticationProvider() {
        // ¡Atención al cambio aquí! Le pasamos la dependencia directamente por el constructor
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());

        // El encriptador de contraseñas sí se sigue pasando con un setter
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    /**
     * Password encoder password encoder.
     *
     * @return the password encoder
     */
// 3. El algoritmo para encriptar/comparar contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication manager authentication manager.
     *
     * @param config the config
     * @return the authentication manager
     * @throws Exception the exception
     */
// 4. El gestor principal que usaremos en nuestro AuthController para hacer el login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}