package com.example.demo.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testa a validação dos tokens sem subir a aplicação.
 */
class TokenServiceTest {

    private final TokenService tokenService = new TokenService("segredo-de-teste", 2);

    @Test
    void recusaTokenCujoSubjectNaoEUmId() {
        // Assinatura válida, mas com o e-mail no lugar do id do usuário
        var token = JWT.create()
                .withIssuer("demo")
                .withSubject("maria@exemplo.com")
                .withExpiresAt(Instant.now().plusSeconds(60))
                .sign(Algorithm.HMAC256("segredo-de-teste"));

        assertThat(tokenService.validarToken(token)).isEmpty();
    }
}
