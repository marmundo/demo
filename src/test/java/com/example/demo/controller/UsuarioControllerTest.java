package com.example.demo.controller;

import com.example.demo.model.Perfil;
import com.example.demo.model.Usuario;
import com.example.demo.repository.InMemoryUsuarioRepository;
import com.example.demo.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Garante que as respostas sobre usuários nunca levam o hash da senha.
 */
class UsuarioControllerTest {

    private final Usuario usuario = new Usuario("Maria", "maria@exemplo.com", "hash-secreto", Perfil.USER);

    // O repositório em memória busca por Long, mas os ids são UUID; aqui a busca é simulada
    private final InMemoryUsuarioRepository repository = new InMemoryUsuarioRepository() {
        @Override
        public Optional<Usuario> buscarPorId(Long id) {
            return Optional.of(usuario);
        }

        @Override
        public Optional<Usuario> deletar(Long id) {
            return Optional.of(usuario);
        }
    };

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new UsuarioController(new UsuarioService(repository)))
            .build();

    @Test
    void buscarNaoExpoeSenha() throws Exception {
        verificarRespostaSemSenha(mockMvc.perform(get("/usuarios/1")));
    }

    @Test
    void atualizarNaoExpoeSenha() throws Exception {
        verificarRespostaSemSenha(mockMvc.perform(put("/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nome": "Maria", "email": "maria@exemplo.com"}
                        """)));
    }

    @Test
    void deletarNaoExpoeSenha() throws Exception {
        verificarRespostaSemSenha(mockMvc.perform(delete("/usuarios/1")));
    }

    private void verificarRespostaSemSenha(ResultActions resposta) throws Exception {
        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("maria@exemplo.com"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.senha").doesNotExist());
    }
}
