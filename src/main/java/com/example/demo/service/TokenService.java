package com.example.demo.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.demo.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Cria e valida os tokens JWT. O token é assinado (HMAC256) com um segredo que só o servidor
 * conhece: o cliente consegue ler o conteúdo, mas não consegue alterá-lo sem invalidar a assinatura.
 */
@Service
public class TokenService {

    // Identifica quem emitiu o token; tokens de outro emissor são recusados
    private static final String EMISSOR = "demo";

    private final Algorithm algoritmo;
    private final Duration validade;

    // @Value lê os valores definidos no application.properties
    public TokenService(@Value("${api.security.token.secret}") String segredo,
                        @Value("${api.security.token.expiracao-horas}") long expiracaoHoras) {
        this.algoritmo = Algorithm.HMAC256(segredo);
        this.validade = Duration.ofHours(expiracaoHoras);
    }

    // O "subject" guarda o id do usuário. Diferente do e-mail, o id nunca muda nem é reaproveitado,
    // então o token de um usuário removido não vale para uma conta nova
    public String gerarToken(Usuario usuario) {
        return JWT.create()
                .withIssuer(EMISSOR)
                .withSubject(usuario.getId().toString())
                .withExpiresAt(Instant.now().plus(validade))
                .sign(algoritmo);
    }

    // Devolve o id contido no token, ou vazio se ele for inválido, adulterado ou expirado
    public Optional<UUID> validarToken(String token) {
        try {
            var subject = JWT.require(algoritmo)
                    .withIssuer(EMISSOR)
                    .build()
                    .verify(token)
                    .getSubject();
            return Optional.ofNullable(subject).map(UUID::fromString);
        } catch (JWTVerificationException | IllegalArgumentException e) {
            // IllegalArgumentException: o subject não é um UUID
            return Optional.empty();
        }
    }
}
