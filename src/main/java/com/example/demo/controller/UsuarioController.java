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


@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criar(@RequestBody UsuarioResquestDTO corpo) {
        UsuarioResponseDTO criada = service.criar(corpo);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        System.out.println("[CONTROLLER] Requisição recebida: GET /tarefas");
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscar(@PathVariable Long id) {
        System.out.println("[CONTROLLER] Requisição recebida: GET /tarefas/" + id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(@PathVariable Long id, @RequestBody UsuarioResquestDTO corpo) {
        System.out.println("Atualizando usuario com id" + id);
        return ResponseEntity.ok(service.atualizar(id,new UsuarioResquestDTO( corpo.nome(), corpo.email(), corpo.cargo())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Usuario> deletar(@PathVariable Long id) {
        return ResponseEntity.ok(service.deletar(id));
    }
}