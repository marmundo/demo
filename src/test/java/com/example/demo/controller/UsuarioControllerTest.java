package com.example.demo.controller;

import com.example.demo.exception.TratadorDeExcecoes;
import com.example.demo.model.Perfil;
import com.example.demo.model.Usuario;
import com.example.demo.repository.InMemoryUsuarioRepository;
import com.example.demo.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testa o CRUD de usuários: busca, atualização e remoção pelo id (UUID), e-mail único
 * e a garantia de que as respostas nunca levam o hash da senha.
 */
class UsuarioControllerTest {

    private final InMemoryUsuarioRepository repository = new InMemoryUsuarioRepository();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final Usuario usuario =
            repository.salvar(new Usuario("Maria", "maria@exemplo.com", "hash-secreto", Perfil.USER));

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new UsuarioController(new UsuarioService(repository, passwordEncoder)))
            .setControllerAdvice(new TratadorDeExcecoes())
            .build();

    @Test
    void buscaUsuarioPeloId() throws Exception {
        verificarRespostaSemSenha(mockMvc.perform(get("/usuarios/{id}", usuario.getId())))
                .andExpect(jsonPath("$.nome").value("Maria"));
    }

    @Test
    void atualizaUsuarioPeloId() throws Exception {
        verificarRespostaSemSenha(atualizar(usuario, """
                {"nome": "Maria Silva", "email": "maria@exemplo.com"}
                """))
                .andExpect(jsonPath("$.nome").value("Maria Silva"));
    }

    @Test
    void removeUsuarioPeloId() throws Exception {
        verificarRespostaSemSenha(mockMvc.perform(delete("/usuarios/{id}", usuario.getId())));

        assertThat(repository.listarTodas()).isEmpty();
    }

    @Test
    void atualizacaoMantemCamposOmitidos() throws Exception {
        atualizar(usuario, """
                {"nome": "Maria Silva"}
                """)
                .andExpect(status().isOk());

        assertThat(usuario.getNome()).isEqualTo("Maria Silva");
        assertThat(usuario.getEmail()).isEqualTo("maria@exemplo.com");
        assertThat(usuario.getPassword()).isEqualTo("hash-secreto");
        assertThat(usuario.getPerfil()).isEqualTo(Perfil.USER);

        atualizar(usuario, """
                {"email": "maria.silva@exemplo.com"}
                """)
                .andExpect(status().isOk());

        assertThat(usuario.getNome()).isEqualTo("Maria Silva");
        assertThat(usuario.getEmail()).isEqualTo("maria.silva@exemplo.com");
    }

    @Test
    void atualizacaoAlteraSenhaEPerfil() throws Exception {
        atualizar(usuario, """
                {"senha": "nova-senha", "perfil": "ADMIN"}
                """)
                .andExpect(status().isOk());

        assertThat(passwordEncoder.matches("nova-senha", usuario.getPassword())).isTrue();
        assertThat(usuario.getPerfil()).isEqualTo(Perfil.ADMIN);
    }

    @Test
    void atualizacaoRecusaCampoEmBrancoSemAlterarNada() throws Exception {
        for (String corpo : new String[]{
                "{\"nome\": \" \"}",
                "{\"nome\": \"Outro\", \"email\": \"\"}",
                "{\"nome\": \"Outro\", \"senha\": \"\"}"}) {
            atualizar(usuario, corpo).andExpect(status().isBadRequest());
        }

        assertThat(usuario.getNome()).isEqualTo("Maria");
        assertThat(usuario.getEmail()).isEqualTo("maria@exemplo.com");
        assertThat(usuario.getPassword()).isEqualTo("hash-secreto");
    }

    @Test
    void criacaoRecusaEmailRepetido() throws Exception {
        // Maiúsculas e minúsculas não diferenciam e-mails, como no login
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome": "Cópia", "email": "MARIA@exemplo.com", "senha": "senha-valida", "perfil": "USER"}
                                """))
                .andExpect(status().isConflict());

        assertThat(repository.listarTodas()).containsExactly(usuario);
    }

    @Test
    void atualizacaoRecusaEmailDeOutroUsuarioSemAlterarNada() throws Exception {
        var joao = repository.salvar(new Usuario("João", "joao@exemplo.com", "hash", Perfil.USER));

        atualizar(joao, """
                {"nome": "Outro", "email": "maria@exemplo.com"}
                """)
                .andExpect(status().isConflict());

        assertThat(joao.getNome()).isEqualTo("João");
        assertThat(joao.getEmail()).isEqualTo("joao@exemplo.com");
    }

    @Test
    void criacaoExigeEmailSenhaEPerfil() throws Exception {
        for (String corpo : new String[]{
                "{\"nome\": \"Ana\", \"senha\": \"senha-valida\", \"perfil\": \"USER\"}",
                "{\"nome\": \"Ana\", \"email\": \" \", \"senha\": \"senha-valida\", \"perfil\": \"USER\"}",
                "{\"nome\": \"Ana\", \"email\": \"ana@exemplo.com\", \"perfil\": \"USER\"}",
                "{\"nome\": \"Ana\", \"email\": \"ana@exemplo.com\", \"senha\": \"\", \"perfil\": \"USER\"}",
                "{\"nome\": \"Ana\", \"email\": \"ana@exemplo.com\", \"senha\": \"senha-valida\"}"}) {
            mockMvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON).content(corpo))
                    .andExpect(status().isBadRequest());
        }

        assertThat(repository.listarTodas()).containsExactly(usuario);
    }

    @Test
    void criacaoExigeSenhaComPeloMenosOitoCaracteres() throws Exception {
        var corpo = """
                {"nome": "Ana", "email": "ana@exemplo.com", "senha": "%s", "perfil": "USER"}
                """;

        mockMvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON)
                        .content(corpo.formatted("1234567")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("A senha deve ter pelo menos 8 caracteres."));
        assertThat(repository.listarTodas()).containsExactly(usuario);

        mockMvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON)
                        .content(corpo.formatted("12345678")))
                .andExpect(status().isCreated());
    }

    @Test
    void atualizacaoExigeSenhaComPeloMenosOitoCaracteres() throws Exception {
        atualizar(usuario, """
                {"nome": "Outro", "senha": "1234567"}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("A senha deve ter pelo menos 8 caracteres."));
        assertThat(usuario.getNome()).isEqualTo("Maria");
        assertThat(usuario.getPassword()).isEqualTo("hash-secreto");

        atualizar(usuario, """
                {"senha": "12345678"}
                """)
                .andExpect(status().isOk());
    }

    @Test
    void usuarioInexistenteRespondeNaoEncontrado() throws Exception {
        var id = UUID.randomUUID();

        for (var requisicao : List.of(
                get("/usuarios/{id}", id),
                put("/usuarios/{id}", id).contentType(MediaType.APPLICATION_JSON).content("{}"),
                delete("/usuarios/{id}", id))) {
            mockMvc.perform(requisicao)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.erro").value("Usuário não encontrado: " + id));
        }
    }

    private ResultActions atualizar(Usuario alvo, String json) throws Exception {
        return mockMvc.perform(put("/usuarios/{id}", alvo.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    private ResultActions verificarRespostaSemSenha(ResultActions resposta) throws Exception {
        return resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("maria@exemplo.com"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.senha").doesNotExist());
    }
}
