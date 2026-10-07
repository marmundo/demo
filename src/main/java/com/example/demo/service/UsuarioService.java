package com.example.demo.service;

import com.example.demo.dto.UsuarioResponseDTO;
import com.example.demo.dto.UsuarioResquestDTO;
import com.example.demo.model.Usuario;
import com.example.demo.model.Tarefa;
import com.example.demo.repository.UsuarioRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Camada de SERVICE para usuários: regras de negócio e conversão entre modelo e DTO.
 * @Service registra a classe como bean gerenciado pelo Spring.
 *
 * @Transactional: cada método roda dentro de uma transação. Se ocorrer uma exceção, as
 * alterações no banco são desfeitas (rollback). Também mantém a sessão do JPA aberta,
 * o que permite acessar a lista "tarefas" (carregada sob demanda) dentro do método.
 */
@Slf4j
@Service
@Transactional
public class UsuarioService {
    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }


    // Cria o usuário: o id é gerado pelo banco, nunca recebido do cliente
    public UsuarioResponseDTO criar(UsuarioResquestDTO dto) {

        Usuario usuario = new Usuario(dto.nome(), dto.email(), dto.cargo());
        Usuario salva = repository.save(usuario);
        return toResponseDTO(salva);
    }

    // Converte o modelo em DTO de resposta
    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getCargo()
        );
    }

    public List<UsuarioResponseDTO> listar() {
        log.info("[SERVICE] Solicitando lista de usuarios ao repository");
        List<UsuarioResponseDTO> respostas = new ArrayList<>();
        for (Usuario usuario : repository.findAll()) {
            respostas.add(toResponseDTO(usuario));
        }
        return respostas;
    }

    // Lança exceção se o usuário não existir
    public Usuario buscarPorId(Long id) {
        log.info("[SERVICE] Processando busca por id: " + id);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario não encontrada: " + id));
    }

    public Usuario atualizar(Long id, UsuarioResquestDTO usuarioResquestDTO) {
        log.info("[SERVICE] Processando atualização por id: " + id);
        Usuario usuarioExistente = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario não encontrada: " + id));
        usuarioExistente.setNome(usuarioResquestDTO.nome());
        usuarioExistente.setEmail(usuarioResquestDTO.email());
        usuarioExistente.setCargo(usuarioResquestDTO.cargo());
        return repository.save(usuarioExistente);
    }

    // Remove e devolve o usuário; se o id não existir, lança exceção.
    // As tarefas do usuário são mantidas, apenas ficam sem responsável.
    // Por que desvincular antes de excluir? Tarefa guarda a chave estrangeira usuario_id.
    // Se o usuário fosse apagado com tarefas ainda apontando para ele, o banco recusaria
    // a operação (violação de integridade referencial). Como Tarefa é a "dona" da relação,
    // basta colocar usuario = null nela; o JPA grava a alteração ao final da transação.
    public Usuario deletar(Long id) {
        log.info("[SERVICE] Processando deleção por id: " + id);
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario não encontrada: " + id));
        for (Tarefa tarefa : usuario.getTarefas()) {
            tarefa.setUsuario(null);
        }
        repository.delete(usuario);
        return usuario;
    }
}
