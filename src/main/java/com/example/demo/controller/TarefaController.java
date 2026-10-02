package com.example.demo.controller;

import com.example.demo.dto.TaskRequestDTO;
import com.example.demo.dto.TaskResponseDTO;
import com.example.demo.service.TarefaService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


/**
 * Camada de CONTROLLER: recebe as requisições HTTP, delega o trabalho ao Service
 * e devolve a resposta. Não contém regra de negócio.
 *
 * @Slf4j (Lombok) cria automaticamente o objeto "log" para registrar mensagens.
 * @RestController = @Controller + @ResponseBody: o retorno dos métodos é convertido em JSON.
 * @RequestMapping define o prefixo de URL de todos os endpoints desta classe.
 */
@Slf4j 
@RestController
@RequestMapping("/tarefas")
public class TarefaController {
    private final TarefaService service;

    // Injeção de dependência via construtor: o Spring cria o TarefaService e o entrega aqui
    public TarefaController(TarefaService service) {
        this.service = service;
    }

    // POST /tarefas -> cria uma tarefa.
    // @RequestBody converte o JSON do corpo da requisição em um TaskRequestDTO.
    // Responde com 201 (Created) e a tarefa criada no corpo.
    @PostMapping
    public ResponseEntity<TaskResponseDTO> criar(@RequestBody TaskRequestDTO corpo) {
     TaskResponseDTO criada=service.criar(corpo);
     return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    // GET /tarefas -> lista todas as tarefas (200 OK)
    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> listar() {
        log.info("[CONTROLLER] Requisição recebida: GET /tarefas");
        return ResponseEntity.ok(service.listar());
    }

    // Retorna todas as tarefas concluidas
    // GET /tarefas/concluidos
    @GetMapping("/concluidos")
    public ResponseEntity<List<TaskResponseDTO>> listarConcluidos(){
       log.info("[CONTROLLER] Requisição recebida: GET /tarefas/concluidos");
       return ResponseEntity.ok(service.listarConcluidos());
    }

    // GET /tarefas/{id} -> busca uma tarefa pelo id.
    // @PathVariable extrai o valor de {id} da URL (ex.: /tarefas/3 -> id = 3).
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> buscar(@PathVariable Long id) {
        log.info("[CONTROLLER] Requisição recebida: GET /tarefas/" + id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }
}
