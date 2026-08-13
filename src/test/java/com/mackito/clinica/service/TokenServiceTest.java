package com.mackito.clinica.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.JWT;
import com.mackito.clinica.model.Usuario;
import com.mackito.clinica.model.PerfilUsuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TokenServiceTest {

    private static final String SEGREDO = "segredo-exclusivo-do-teste-com-tamanho-adequado-123456";

    @Test
    void deveGerarEValidarTokenParaEmailDoUsuario() {
        TokenService tokenService = new TokenService(SEGREDO);
        Usuario usuario = new Usuario("paciente@example.com", "hash-nao-utilizado-no-token", PerfilUsuario.PACIENTE);

        String token = tokenService.gerarToken(usuario);

        assertEquals("paciente@example.com", tokenService.validarToken(token));
        assertEquals("PACIENTE", JWT.decode(token).getClaim("perfil").asString());
    }

    @Test
    void deveRejeitarTokenAssinadoComOutroSegredo() {
        TokenService emissor = new TokenService(SEGREDO);
        TokenService validador = new TokenService("outro-segredo-exclusivo-do-teste-com-tamanho-123456");
        Usuario usuario = new Usuario("paciente@example.com", "hash-nao-utilizado-no-token", PerfilUsuario.PACIENTE);

        String token = emissor.gerarToken(usuario);

        assertThrows(JWTVerificationException.class, () -> validador.validarToken(token));
    }

    @Test
    void deveRejeitarSegredoFraco() {
        assertThrows(IllegalArgumentException.class, () -> new TokenService("segredo-curto"));
    }
}
