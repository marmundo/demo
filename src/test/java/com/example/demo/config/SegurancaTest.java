package com.example.demo.config;

import com.example.demo.model.Perfil;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.TokenService;
import jakarta.servlet.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testa as regras de acesso com a aplicação inteira, incluindo a cadeia de filtros de segurança.
 */
@SpringBootTest(properties = {
        "api.security.admin.email=admin@exemplo.com",
        "api.security.admin.senha=senha-do-admin"
})
class SegurancaTest {

    @Autowired
    private WebApplicationContext contexto;
    @Autowired
    @Qualifier("springSecurityFilterChain")
    private Filter filtroDeSeguranca;
    @Autowired
    private UsuarioRepository repository;
    @Autowired
    private TokenService tokenService;

    private MockMvc mockMvc;

    @BeforeEach
    void configurar() {
        mockMvc = MockMvcBuilders.webAppContextSetup(contexto).addFilters(filtroDeSeguranca).build();
    }

    @Test
    void registroPublicoSempreCriaUsuarioComum() throws Exception {
        enviar("/auth/registrar", """
                {"nome": "Intruso", "email": "intruso@exemplo.com", "senha": "senha-qualquer", "perfil": "ADMIN"}
                """)
                .andExpect(status().isCreated());

        assertThat(repository.findByEmail("intruso@exemplo.com"))
                .get()
                .extracting(Usuario::getPerfil)
                .isEqualTo(Perfil.USER);
    }

    @Test
    void usuarioComumNaoAcessaRotasDeUsuarios() throws Exception {
        var token = tokenDeUsuarioComum("comum@exemplo.com");

        mockMvc.perform(get("/usuarios").header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/usuarios")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome": "Falso", "email": "falso@exemplo.com", "senha": "x", "perfil": "ADMIN"}
                                """))
                .andExpect(status().isForbidden());

        assertThat(repository.findByEmail("falso@exemplo.com")).isEmpty();
    }

    @Test
    void administradorInicialAcessaRotasDeUsuarios() throws Exception {
        var admin = repository.findByEmail("admin@exemplo.com").orElseThrow();
        assertThat(admin.getPerfil()).isEqualTo(Perfil.ADMIN);

        // O login confirma que a senha do administrador foi gravada como hash BCrypt
        enviar("/auth/login", """
                {"email": "admin@exemplo.com", "senha": "senha-do-admin"}
                """)
                .andExpect(status().isOk());

        mockMvc.perform(get("/usuarios")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenService.gerarToken(admin)))
                .andExpect(status().isOk());
    }

    @Test
    void usuarioCriadoPeloAdministradorTemSenhaComHashEFazLogin() throws Exception {
        var admin = repository.findByEmail("admin@exemplo.com").orElseThrow();

        mockMvc.perform(post("/usuarios")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenService.gerarToken(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome": "Novo", "email": "novo@exemplo.com", "senha": "senha-do-novo", "perfil": "USER"}
                                """))
                .andExpect(status().isCreated());

        assertThat(repository.findByEmail("novo@exemplo.com").orElseThrow().getPassword())
                .isNotEqualTo("senha-do-novo");
        enviar("/auth/login", """
                {"email": "novo@exemplo.com", "senha": "senha-do-novo"}
                """)
                .andExpect(status().isOk());
    }

    private ResultActions enviar(String rota, String json) throws Exception {
        return mockMvc.perform(post(rota).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private String tokenDeUsuarioComum(String email) {
        var usuario = repository.salvar(new Usuario("Comum", email, "hash-irrelevante", Perfil.USER));
        return "Bearer " + tokenService.gerarToken(usuario);
    }
}
