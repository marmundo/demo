package com.example.demo.config;

import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro executado uma vez por requisição: se houver um token válido no header
 * "Authorization: Bearer ...", identifica o usuário para o Spring Security.
 * Ele não bloqueia nada; quem decide o acesso são as regras do SecurityConfig.
 */
@Component
public class SecurityFilter extends OncePerRequestFilter {

    private static final String PREFIXO = "Bearer ";

    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    public SecurityFilter(TokenService tokenService, UsuarioRepository usuarioRepository) {
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        var cabecalho = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (cabecalho != null && cabecalho.startsWith(PREFIXO)) {
            tokenService.validarToken(cabecalho.substring(PREFIXO.length()))
                    .flatMap(usuarioRepository::buscarPorId)
                    .ifPresent(usuario -> {
                        var autenticacao = new UsernamePasswordAuthenticationToken(
                                usuario, null, usuario.getAuthorities());
                        var contexto = SecurityContextHolder.createEmptyContext();
                        contexto.setAuthentication(autenticacao);
                        SecurityContextHolder.setContext(contexto);
                    });
        }

        // Passa a requisição adiante, autenticada ou não
        filterChain.doFilter(request, response);
    }
}
