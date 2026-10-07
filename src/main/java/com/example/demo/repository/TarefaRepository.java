package com.example.demo.repository;

import com.example.demo.model.Tarefa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Camada de REPOSITORY: o Spring Data JPA gera a implementação em tempo de execução.
 * Já herda save, findAll, findById, deleteById etc.; as consultas abaixo são
 * derivadas do nome do método.
 */
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
    // Query derivada: findBy + Concluida + True  ->  WHERE concluida = true
    List<Tarefa> findByConcluidaTrue();

    // Navega pela relação: findBy + Usuario + Id -> tarefa.getUsuario().getId(),
    // que no SQL vira WHERE usuario_id = ?
    List<Tarefa> findByUsuarioId(Long usuarioId);
}
