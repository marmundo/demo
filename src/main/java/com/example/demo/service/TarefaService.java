package com.example.demo.service;

import com.example.demo.dto.TaskRequestDTO;
import com.example.demo.dto.TaskResponseDTO;
import com.example.demo.exception.RecursoNaoEncontradoException;
import com.example.demo.exception.TarefaNaoEncontradaException;
import com.example.demo.model.Prioridade;
import com.example.demo.model.Tarefa;
import com.example.demo.model.Usuario;
import com.example.demo.repository.PrioridadeRepository;
import com.example.demo.repository.TarefaRepository;
import com.example.demo.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Camada de SERVICE: concentra as regras de negócio (validações, valores padrão)
 * e faz a conversão entre entidade (Tarefa) e DTO.
 * Fluxo de uma requisição: Controller -> Service -> Repository.
 *
 * @Transactional mantém a sessão do JPA aberta durante o método, permitindo
 * ler as relações carregadas sob demanda (LAZY) ao montar o DTO.
 */
@Slf4j
@Service
@Transactional
public class TarefaService {
    private final TarefaRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final PrioridadeRepository prioridadeRepository;

    // Injeção de dependência via construtor
    public TarefaService(TarefaRepository repository,
                         UsuarioRepository usuarioRepository,
                         PrioridadeRepository prioridadeRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.prioridadeRepository = prioridadeRepository;
    }

    // Cria uma tarefa a partir do DTO de entrada.
    // O cliente envia apenas os IDs (prioridadeId, usuarioId); aqui buscamos as entidades
    // completas no banco, pois o JPA precisa dos objetos para montar as chaves estrangeiras.
    public TaskResponseDTO criar(TaskRequestDTO dto) {
        String titulo = dto.titulo();
        log.info("[SERVICE] Validando regra de negócio para: {}", titulo);
        // Regra de negócio: título é obrigatório
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("O título da tarefa não pode ser vazio.");
        }
        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo(titulo);
        // Regra de negócio: sem prioridade informada, a tarefa nova começa com BAIXA
        tarefa.setPrioridade(dto.prioridadeId() != null
                ? buscarPrioridade(dto.prioridadeId())
                : prioridadePadrao());
        if (dto.usuarioId() != null) {
            tarefa.setUsuario(buscarUsuario(dto.usuarioId()));
        }
        return toResponseDTO(repository.save(tarefa));
    }

    // Prioridade usada quando o cliente não informa nenhuma (registro criado pelo data.sql)
    private Prioridade prioridadePadrao() {
        return prioridadeRepository.findByNome("BAIXA")
                .orElseThrow(() -> new IllegalStateException("Prioridade padrão BAIXA não cadastrada."));
    }

    private Prioridade buscarPrioridade(Long id) {
        return prioridadeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Prioridade", id));
    }

    // Se o id não existir, lançamos exceção: o TratadorDeExcecoes a converte em HTTP 404.
    // Validar aqui evita gravar uma tarefa apontando para um usuário que não existe.
    private Usuario buscarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", id));
    }

    // Converte a entidade em DTO de resposta, para não expor o modelo interno ao cliente
    private TaskResponseDTO toResponseDTO(Tarefa tarefa) {
        // prioridade e usuario são carregados sob demanda (LAZY) ao chamar getNome()/getId();
        // por isso este método precisa rodar dentro da transação da classe.
        // Ambos podem ser null (tarefa sem dono), então testamos antes de acessá-los.
        Prioridade prioridade = tarefa.getPrioridade();
        Usuario usuario = tarefa.getUsuario();
        return new TaskResponseDTO(
                tarefa.getId(),
                tarefa.getTitulo(),
                tarefa.isConcluida(),
                prioridade != null ? prioridade.getNome() : null,
                usuario != null ? usuario.getId() : null,
                usuario != null ? usuario.getNome() : null
        );
    }

    // Busca todas as tarefas e converte cada uma para DTO
    public List<TaskResponseDTO> listar() {
        log.info("[SERVICE] Solicitando lista de tarefas ao repository");
        return repository.findAll().stream().map(this::toResponseDTO).toList();
    }

    // Busca por id; se não existir, lança exceção (orElseThrow trata o Optional vazio)
    public TaskResponseDTO buscarPorId(Long id) {
        log.info("[SERVICE] Processando busca por id: {}", id);
        Tarefa tarefa = repository.findById(id)
                .orElseThrow(() -> new TarefaNaoEncontradaException(id));
        return toResponseDTO(tarefa);
    }

    // Apenas as tarefas marcadas como concluídas. O filtro é feito pelo banco (WHERE concluida = true),
    // não em Java: é mais eficiente, pois só as linhas necessárias saem do banco.
    public List<TaskResponseDTO> listarConcluidos() {
        log.info("[SERVICE] Solicitando lista de tarefas concluidas");
        return repository.findByConcluidaTrue().stream().map(this::toResponseDTO).toList();
    }

    // Tarefas de um usuário; valida antes que o usuário exista
    public List<TaskResponseDTO> listarPorUsuario(Long usuarioId) {
        log.info("[SERVICE] Solicitando tarefas do usuário: {}", usuarioId);
        buscarUsuario(usuarioId);
        return repository.findByUsuarioId(usuarioId).stream().map(this::toResponseDTO).toList();
    }

    public TaskResponseDTO atualizar(Long id, TaskRequestDTO dto) {
        log.info("[SERVICE] Processando atualização da tarefa com id: {}", id);
        Tarefa tarefaExistente = repository.findById(id)
                .orElseThrow(() -> new TarefaNaoEncontradaException(id));

        // Atualiza os campos da tarefa existente com os valores do DTO
        tarefaExistente.setTitulo(dto.titulo());
        // Prioridade e usuário só mudam se informados; caso contrário, mantém os existentes
        if (dto.prioridadeId() != null) {
            tarefaExistente.setPrioridade(buscarPrioridade(dto.prioridadeId()));
        }
        if (dto.usuarioId() != null) {
            tarefaExistente.setUsuario(buscarUsuario(dto.usuarioId()));
        }
        tarefaExistente.setConcluida(dto.concluida());

        return toResponseDTO(repository.save(tarefaExistente));
    }
}
