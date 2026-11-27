package com.example.demo.config;

import com.example.demo.model.Perfil;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.UsuarioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Cria o primeiro administrador quando a aplicação inicia. Como o cadastro público só gera
 * usuários comuns, este é o caminho para existir um ADMIN.
 * E-mail e senha vêm das variáveis de ambiente ADMIN_EMAIL e ADMIN_SENHA, para que
 * nenhuma credencial fique gravada no código.
 */
@Slf4j
@Component
public class AdminInicial implements ApplicationRunner {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String senha;

    public AdminInicial(UsuarioRepository repository,
                        PasswordEncoder passwordEncoder,
                        @Value("${api.security.admin.email}") String email,
                        @Value("${api.security.admin.senha}") String senha) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.email = email;
        this.senha = senha;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (email.isBlank() || senha.isBlank()) {
            log.info("ADMIN_EMAIL e ADMIN_SENHA não definidas: nenhum administrador foi criado");
            return;
        }
        // A aplicação continua subindo; a senha não é escrita no log
        if (senha.length() < UsuarioService.TAMANHO_MINIMO_SENHA) {
            log.warn("ADMIN_SENHA tem menos de {} caracteres: nenhum administrador foi criado",
                    UsuarioService.TAMANHO_MINIMO_SENHA);
            return;
        }
        if (repository.findByEmail(email).isEmpty()) {
            repository.salvar(new Usuario("Administrador", email, passwordEncoder.encode(senha), Perfil.ADMIN));
            log.info("Administrador inicial criado");
        }
    }
}
