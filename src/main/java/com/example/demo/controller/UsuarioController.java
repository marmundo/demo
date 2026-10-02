package com.example.demo.controller;

import com.example.demo.dto.UsuarioResponseDTO;
import com.example.demo.dto.UsuarioResquestDTO;
import com.example.demo.model.Usuario;
import com.example.demo.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import lombok.extern.slf4j.Slf4j;


/**
 * Camada de CONTROLLER para o recurso "usuários" (CRUD completo: POST, GET, PUT, DELETE).
 * Cada método mapeia um verbo HTTP + URL para uma operação do Service.
 */
@Slf4j
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    private final UsuarioService service;

    // Injeção de dependência via construtor
    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    // POST /usuarios -> cria um usuário e responde 201 (Created)
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criar(@RequestBody UsuarioResquestDTO corpo) {
        UsuarioResponseDTO criada = service.criar(corpo);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    // GET /usuarios -> lista todos os usuários
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
        log.info("[CONTROLLER] Requisição recebida: GET /tarefas");
        return ResponseEntity.ok(service.listar());
    }

    // GET /usuarios/{id} -> busca um usuário; @PathVariable captura o {id} da URL
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscar(@PathVariable Long id) {
        log.info("[CONTROLLER] Requisição recebida: GET /tarefas/" + id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // PUT /usuarios/{id} -> atualiza um usuário (id vem da URL, novos dados vêm no corpo JSON)
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(@PathVariable Long id, @RequestBody UsuarioResquestDTO corpo) {
        log.info("[CONTROLLER] Requisição recebida: PUT /tarefas/" + id);
        return ResponseEntity.ok(service.atualizar(id,new UsuarioResquestDTO( corpo.nome(), corpo.email(), corpo.cargo())));
    }

    // DELETE /usuarios/{id} -> remove o usuário e devolve o registro removido
    @DeleteMapping("/{id}")
    public ResponseEntity<Usuario> deletar(@PathVariable Long id) {
        log.info("[CONTROLLER] Requisição recebida: DELETE /tarefas/" + id);
        return ResponseEntity.ok(service.deletar(id));
    }
}
