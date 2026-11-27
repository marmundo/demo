package com.example.demo.config;

import com.example.demo.model.Perfil;
import com.example.demo.model.Usuario;
import com.example.demo.repository.InMemoryUsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testa a criação do administrador inicial sem subir a aplicação.
 */
class AdminInicialTest {

    private final InMemoryUsuarioRepository repository = new InMemoryUsuarioRepository();

    @Test
    void naoCriaAdministradorComSenhaDeMenosDeOitoCaracteres() {
        executar("1234567");

        assertThat(repository.listarTodas()).isEmpty();
    }

    @Test
    void criaAdministradorComSenhaDeOitoCaracteres() {
        executar("12345678");

        assertThat(repository.findByEmail("admin@exemplo.com"))
                .get()
                .extracting(Usuario::getPerfil)
                .isEqualTo(Perfil.ADMIN);
    }

    private void executar(String senha) {
        new AdminInicial(repository, new BCryptPasswordEncoder(), "admin@exemplo.com", senha)
                .run(new DefaultApplicationArguments());
    }
}
