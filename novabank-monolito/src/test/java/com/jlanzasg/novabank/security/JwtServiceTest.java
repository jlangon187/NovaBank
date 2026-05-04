package com.jlanzasg.novabank.security;

import com.jlanzasg.novabank.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private final String SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"; // Base64 key
    private final long EXPIRATION = 3600000;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", EXPIRATION);
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        UserDetails user = Usuario.builder()
                .email("test@example.com")
                .build();

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertEquals("test@example.com", jwtService.extractUsername(token));
    }

    @Test
    void isTokenValid_ShouldReturnTrueForCorrectUser() {
        UserDetails user = Usuario.builder()
                .email("test@example.com")
                .build();

        String token = jwtService.generateToken(user);

        assertTrue(jwtService.isTokenValid(token, user));
    }

    @Test
    void isTokenValid_ShouldReturnFalseForWrongUser() {
        UserDetails user1 = Usuario.builder()
                .email("test1@example.com")
                .build();
        UserDetails user2 = Usuario.builder()
                .email("test2@example.com")
                .build();

        String token = jwtService.generateToken(user1);

        assertFalse(jwtService.isTokenValid(token, user2));
    }
}
