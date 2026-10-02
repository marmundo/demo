package com.example.demo.service;

import com.example.demo.dto.TaskRequestDTO;
import com.example.demo.dto.TaskResponseDTO;
import com.example.demo.exception.TarefaNaoEncontradaException;
import com.example.demo.model.Prioridade;
import com.example.demo.model.Tarefa;
import com.example.demo.repository.TarefaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Camada de SERVICE: concentra as regras de negócio (validações, valores padrão)
 * e faz a conversão entre entidade (Tarefa) e DTO.
 * Fluxo de uma requisição: Controller -> Service -> Repository.
 */
@Slf4j
@Service
public class TarefaService {
    private final TarefaRepository repository;

    // Gerador de ids sequenciais. AtomicLong é seguro quando várias requisições chegam ao mesmo tempo.
    private final AtomicLong sequencia = new AtomicLong();

    // Injeção de dependência via construtor
    public TarefaService(TarefaRepository repository) {
        this.repository = repository;
    }


    // Cria uma tarefa a partir do DTO de entrada
    public TaskResponseDTO criar(TaskRequestDTO dto) {
        String titulo=dto.titulo();
        Tarefa tarefa = new Tarefa();
        tarefa.setId(sequencia.incrementAndGet());
        tarefa.setTitulo(titulo);
        // Regra de negócio: toda tarefa nova começa com prioridade BAIXA
        tarefa.setPrioridade(Prioridade.BAIXA);
        log.info("[SERVICE] Validando regra de negócio para: {}", titulo);
        // Regra de negócio: título é obrigatório
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("O título da tarefa não pode ser vazio.");
        }
        Tarefa salva= repository.salvar(tarefa);
        return toResponseDTO(salva);
    }

    // Converte a entidade em DTO de resposta, para não expor o modelo interno ao cliente
    private TaskResponseDTO toResponseDTO(Tarefa tarefa) {
        return new TaskResponseDTO(
                tarefa.getId(),
                tarefa.getTitulo(),
                tarefa.isConcluida(),
                tarefa.getPrioridade().toString()
        );
    }

    // Busca todas as tarefas e converte cada uma para DTO
    public List<TaskResponseDTO> listar() {
        log.info("[SERVICE] Solicitando lista de tarefas ao repository");
        List<TaskResponseDTO> respostas = new ArrayList<>();
        for (Tarefa tarefa : repository.listarTodas()) {
            respostas.add(toResponseDTO(tarefa));
        }
        return respostas;
    }

    // Busca por id; se não existir, lança exceção (orElseThrow trata o Optional vazio)
    public TaskResponseDTO buscarPorId(Long id) {
        log.info("[SERVICE] Processando busca por id: {}", id);
        Tarefa tarefa = repository.buscarPorId(id)
                .orElseThrow(() -> new TarefaNaoEncontradaException(id));
        return toResponseDTO(tarefa);
    }

    // Filtra, na camada de serviço, apenas as tarefas marcadas como concluídas
    public List<TaskResponseDTO> listarConcluidos(){
        log.info("[SERVICE] Solicitando lista de tarefas concluidas");
        List<TaskResponseDTO> tarefasConcluidas = new ArrayList<>();

        for (Tarefa tarefa : repository.listarTodas()) {
            if (tarefa.isConcluida()) {
                tarefasConcluidas.add(toResponseDTO(tarefa));
            }
        }
        return tarefasConcluidas;
    }
}
