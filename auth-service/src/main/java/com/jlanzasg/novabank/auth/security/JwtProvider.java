package com.jlanzasg.novabank.auth.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtProvider {

    // Clave secreta (en prod debería estar en el application.yml o Config Server)
    @Value("${jwt.secret:EstaEsUnaClaveSecretaMuyLargaYRequeteseguraParaNovaBank2026!}")
    private String secret;

    @Value("${jwt.expiration:3600000}") // 1 hora por defecto
    private long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            System.out.println("Error validando token: " + e.getMessage());
            return false;
        }
    }
}