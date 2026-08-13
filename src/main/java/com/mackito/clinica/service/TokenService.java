package com.mackito.clinica.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.mackito.clinica.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    private static final int TAMANHO_MINIMO_SEGREDO = 32;

    private final String secret;

    public TokenService(@Value("${app.jwt.secret}") String secret) {
        if (secret == null || secret.isBlank() || secret.length() < TAMANHO_MINIMO_SEGREDO) {
            throw new IllegalArgumentException("app.jwt.secret deve possuir pelo menos 32 caracteres");
        }
        this.secret = secret;
    }

    public String gerarToken(Usuario usuario) {
        return JWT.create()
                .withSubject(usuario.getEmail())
                .withExpiresAt(dataExpiracao())
                .sign(Algorithm.HMAC256(secret));
    }

    public String validarToken(String token) {
        return JWT.require(Algorithm.HMAC256(secret))
                .build()
                .verify(token)
                .getSubject();
    }

    private Instant dataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
