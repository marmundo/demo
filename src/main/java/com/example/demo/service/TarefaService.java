package com.example.demo.service;

import com.example.demo.dto.TaskRequestDTO;
import com.example.demo.dto.UsuarioResquestDTO;
import com.example.demo.dto.TaskResponseDTO;
import com.example.demo.model.Prioridade;
import com.example.demo.model.Tarefa;
import com.example.demo.repository.TarefaRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TarefaService {
    private final TarefaRepository repository;
    private final AtomicLong sequencia = new AtomicLong();

    public TarefaService(TarefaRepository repository) {
        this.repository = repository;
    }


    public TaskResponseDTO criar(TaskRequestDTO dto) {
        String titulo=dto.titulo();
        Tarefa tarefa = new Tarefa(null,dto.titulo(),false, Prioridade.BAIXA,dto.usuario());
        System.out.println("[SERVICE] Validando regra de negócio para: " +
                titulo);
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("O título da tarefa não pode ser vazio.");
        }
        Tarefa salva= repository.save(tarefa);
        return toResponseDTO(salva);
    }

    private TaskResponseDTO toResponseDTO(Tarefa tarefa) {
        return new TaskResponseDTO(
                tarefa.getId(),
                tarefa.getTitulo(),
                tarefa.isConcluida(),
                tarefa.getPrioridade()
        );
    }

    public List<Tarefa> listar() {
        System.out.println("[SERVICE] Solicitando lista de tarefas ao repository");
        return repository.findAll();
    }
    public Tarefa buscarPorId(Long id) {
        System.out.println("[SERVICE] Processando busca por id: " + id);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tarefa não encontrada: " + id));
    }

    public List<Tarefa> listarConcluidos(){
        System.out.println("[SERVICE] Solicitando lista de tarefas concluidas");
        List<Tarefa> tarefas = listar();
        List<Tarefa> tarefasConcluidas=new ArrayList<Tarefa>();

        for(Tarefa tarefa: tarefas){
            if(tarefa.isConcluida()){
                tarefasConcluidas.add(tarefa);
            }
        }
        return tarefasConcluidas;

    }

    public TaskResponseDTO atualizar(Long id, TaskRequestDTO corpo) {
        Tarefa tarefa=buscarPorId(id);
       tarefa.setTitulo(corpo.titulo());
       tarefa.setConcluida(corpo.concluida());
       tarefa.setPrioridade(corpo.prioridade());
       return toResponseDTO(repository.save(tarefa));
    }
}