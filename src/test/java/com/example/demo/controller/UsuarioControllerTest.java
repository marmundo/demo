package com.example.demo.controller;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testa a busca, a atualização e a remoção de usuários pelo id (UUID)
 * e garante que as respostas nunca levam o hash da senha.
 */
class UsuarioControllerTest {

    private final InMemoryUsuarioRepository repository = new InMemoryUsuarioRepository();
    private final Usuario usuario =
            repository.salvar(new Usuario("Maria", "maria@exemplo.com", "hash-secreto", Perfil.USER));

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new UsuarioController(
                    new UsuarioService(repository, new BCryptPasswordEncoder())))
            .build();

    @Test
    void buscaUsuarioPeloId() throws Exception {
        verificarRespostaSemSenha(mockMvc.perform(get("/usuarios/{id}", usuario.getId())))
                .andExpect(jsonPath("$.nome").value("Maria"));
    }

    @Test
    void atualizaUsuarioPeloId() throws Exception {
        verificarRespostaSemSenha(mockMvc.perform(put("/usuarios/{id}", usuario.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nome": "Maria Silva", "email": "maria@exemplo.com"}
                        """)))
                .andExpect(jsonPath("$.nome").value("Maria Silva"));
    }

    @Test
    void removeUsuarioPeloId() throws Exception {
        verificarRespostaSemSenha(mockMvc.perform(delete("/usuarios/{id}", usuario.getId())));

        assertThat(repository.listarTodas()).isEmpty();
    }

    private ResultActions verificarRespostaSemSenha(ResultActions resposta) throws Exception {
        return resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("maria@exemplo.com"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.senha").doesNotExist());
    }
}
