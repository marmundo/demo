package com.example.demo.controller;

import com.example.demo.repository.TarefaRepository;
import com.example.demo.service.TarefaService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testa o endpoint de tarefas sem subir a aplicação inteira (e, portanto, sem a segurança).
 */
class TarefaControllerTest {

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new TarefaController(new TarefaService(new TarefaRepository())))
            .build();

    @Test
    void criaTarefaNaoConcluidaSemInformarConcluida() throws Exception {
        mockMvc.perform(post("/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"titulo": "Estudar Spring Boot"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.concluida").value(false));
    }

    @Test
    void ignoraConcluidaEnviadaNaCriacao() throws Exception {
        mockMvc.perform(post("/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"titulo": "Estudar Spring Boot", "concluida": true}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.concluida").value(false));
    }

    @Test
    void mantemConcluidaQuandoOmitidaNaAtualizacao() throws Exception {
        criarTarefa();
        atualizarPrimeiraTarefa("""
                {"titulo": "Estudar Spring Boot", "concluida": true}
                """);

        atualizarPrimeiraTarefa("""
                {"titulo": "Revisar Spring Boot"}
                """)
                .andExpect(jsonPath("$.concluida").value(true));
    }

    @Test
    void desmarcaConcluidaQuandoEnviadaComoFalse() throws Exception {
        criarTarefa();
        atualizarPrimeiraTarefa("""
                {"titulo": "Estudar Spring Boot", "concluida": true}
                """);

        atualizarPrimeiraTarefa("""
                {"titulo": "Estudar Spring Boot", "concluida": false}
                """)
                .andExpect(jsonPath("$.concluida").value(false));
    }

    private void criarTarefa() throws Exception {
        mockMvc.perform(post("/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"titulo": "Estudar Spring Boot"}
                                """))
                .andExpect(status().isCreated());
    }

    // Cada teste usa um repositório novo, então a primeira tarefa criada tem id 1
    private ResultActions atualizarPrimeiraTarefa(String json) throws Exception {
        return mockMvc.perform(put("/tarefas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }
}
