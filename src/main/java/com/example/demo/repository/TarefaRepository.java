package com.example.demo.repository;

import com.example.demo.model.Tarefa;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Repository;
import java.util.*;

/**
 * Camada de REPOSITORY: responsável apenas por guardar e recuperar tarefas.
 * Guarda as tarefas em um Map em memória.
 */
@Slf4j 
@Repository
public class TarefaRepository {
    // "Banco de dados" simulado: chave = id da tarefa. LinkedHashMap preserva a ordem de inserção.
    private final Map<Long, Tarefa> banco = new LinkedHashMap<>();

    public Tarefa salvar(Tarefa tarefa) {
        log.info("[REPOSITORY] Salvando tarefa em memória: " + tarefa.getTitulo());
        banco.put(tarefa.getId(),tarefa);
        return tarefa;
    }

    // Devolve uma cópia da lista para proteger o mapa interno
    public List<Tarefa> listarTodas() {
        log.info("[REPOSITORY] Buscando todas as tarefas em memória");
        return new ArrayList<>(banco.values());
    }

    // Optional vazio quando o id não existe; o Service decide o que fazer nesse caso
    public Optional<Tarefa> buscarPorId(Long id) {
        log.info("[REPOSITORY] Buscando tarefa por id: " + id);
        return Optional.ofNullable(banco.get(id));
    }
}
