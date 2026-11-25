package com.example.demo.config;

import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration       // classe que declara beans de configuração
@EnableWebSecurity   // ativa a configuração de segurança web personalizada
public class SecurityConfig<SecurityFilter> {

    // Nosso filtro que lê o token JWT (veja a Etapa 12)
    private final SecurityFilter securityFilter;

    public SecurityConfig(@Qualifier("") SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    // Define a "cadeia de catracas" pela qual toda requisição passa
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // CSRF protege contra abuso de cookies de sessão. Como usamos token no header
                // (e não cookie), o ataque não se aplica e a proteção só atrapalharia
                .csrf(csrf -> csrf.disable())
                // STATELESS: o servidor não cria sessão (HttpSession). Cada requisição se prova sozinha pelo token
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Login e cadastro precisam ser públicos; senão ninguém conseguiria obter o primeiro token
                        .requestMatchers(HttpMethod.POST, "/auth/login", "/auth/registrar").permitAll()
                        // Só quem tem o perfil ADMIN pode criar produtos
                        .requestMatchers(HttpMethod.POST, "/produtos").hasRole("ADMIN")
                        // Todo o resto exige apenas estar autenticado. A ordem das regras importa: vale a primeira que casar
                        .anyRequest().authenticated())
                // Nosso filtro precisa rodar ANTES do filtro padrão de login por formulário,
                // para já deixar o usuário identificado quando as regras acima forem avaliadas
                .addFilterBefore((Filter) securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // Expõe o AuthenticationManager que o Spring já monta, para o AuthController poder injetá-lo
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // BCrypt aplica um "sal" aleatório e é propositalmente lento, o que dificulta ataques de força bruta
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}