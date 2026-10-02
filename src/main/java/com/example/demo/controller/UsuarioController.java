package com.example.demo.controller;

import com.example.demo.dto.TaskResponseDTO;
import com.example.demo.dto.UsuarioResponseDTO;
import com.example.demo.dto.UsuarioResquestDTO;
import com.example.demo.model.Usuario;
import com.example.demo.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


/**
 * Camada de CONTROLLER para o recurso "usuários" (CRUD completo: POST, GET, PUT, DELETE).
 * Cada método mapeia um verbo HTTP + URL para uma operação do Service.
 */
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
    public ResponseEntity<List<Usuario>> listar() {
        System.out.println("[CONTROLLER] Requisição recebida: GET /tarefas");
        return ResponseEntity.ok(service.listar());
    }

    // GET /usuarios/{id} -> busca um usuário; @PathVariable captura o {id} da URL
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscar(@PathVariable Long id) {
        System.out.println("[CONTROLLER] Requisição recebida: GET /tarefas/" + id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // PUT /usuarios/{id} -> atualiza um usuário (id vem da URL, novos dados vêm no corpo JSON)
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(@PathVariable Long id, @RequestBody UsuarioResquestDTO corpo) {
        System.out.println("Atualizando usuario com id" + id);
        return ResponseEntity.ok(service.atualizar(id,new UsuarioResquestDTO( corpo.nome(), corpo.email(), corpo.cargo())));
    }

    // DELETE /usuarios/{id} -> remove o usuário e devolve o registro removido
    @DeleteMapping("/{id}")
    public ResponseEntity<Usuario> deletar(@PathVariable Long id) {
        return ResponseEntity.ok(service.deletar(id));
    }
}
