package com.example.demo.service;

import com.example.demo.dto.UsuarioResponseDTO;
import com.example.demo.dto.UsuarioResquestDTO;
import com.example.demo.model.Usuario;
import com.example.demo.repository.InMemoryUsuarioRepository;
import com.example.demo.repository.UsuarioRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Camada de SERVICE para usuários: regras de negócio e conversão entre modelo e DTO.
 * @Service registra a classe como bean gerenciado pelo Spring.
 */
@Slf4j 
@Service
public class UsuarioService {
    // O atributo usa a interface (abstração); o construtor recebe a implementação concreta
    private final UsuarioRepository repository;
    // Gerador de ids sequenciais, seguro para uso concorrente
    private final AtomicLong sequencia = new AtomicLong();

    public UsuarioService(InMemoryUsuarioRepository repository) {
        this.repository = repository;
    }


    // Cria o usuário: o id é gerado aqui, nunca recebido do cliente
    public UsuarioResponseDTO criar(UsuarioResquestDTO dto) {

        Usuario usuario = new Usuario(sequencia.incrementAndGet(),dto.nome(),dto.email(),dto.cargo());
        Usuario salva= repository.salvar(usuario);
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
        for (Usuario usuario : repository.listarTodas()) {
            respostas.add(toResponseDTO(usuario));
        }
        return respostas;
    }

    // Lança exceção se o usuário não existir
    public Usuario buscarPorId(Long id) {
        log.info("[SERVICE] Processando busca por id: " + id);
        return repository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario não encontrada: " + id));
    }

    public Usuario atualizar(Long id, UsuarioResquestDTO usuarioResquestDTO) {
        log.info("[SERVICE] Processando atualização por id: " + id);
        Usuario usuarioExistente = repository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario não encontrada: " + id));
        usuarioExistente.setNome(usuarioResquestDTO.nome());
        usuarioExistente.setEmail(usuarioResquestDTO.email());
        usuarioExistente.setCargo(usuarioResquestDTO.cargo());
        return repository.atualizar(usuarioExistente);
    }

    // Remove e devolve o usuário; se o id não existir, lança exceção
    public Usuario deletar(Long id) {
        log.info("[SERVICE] Processando deleção por id: " + id);
        return (Usuario) repository.deletar(id).orElseThrow(() -> new IllegalArgumentException("Usuario não encontrada: " + id));
    }
}
