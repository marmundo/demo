package com.example.demo.service;

import com.example.demo.dto.UsuarioResponseDTO;
import com.example.demo.dto.UsuarioResquestDTO;
import com.example.demo.model.Usuario;
import com.example.demo.repository.InMemoryUsuarioRepository;
import com.example.demo.repository.UsuarioRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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

    // Usado para gravar apenas o hash da senha
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(InMemoryUsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }


    // Cria o usuário: o id é gerado aqui, nunca recebido do cliente
    public UsuarioResponseDTO criar(UsuarioResquestDTO dto) {

        var senhaHash = passwordEncoder.encode(dto.senha());
        Usuario usuario = new Usuario(dto.nome(),dto.email(),senhaHash,dto.perfil());
        Usuario salva= repository.salvar(usuario);
        return toResponseDTO(salva);
    }

    // Converte o modelo em DTO de resposta
    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil()
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
    public UsuarioResponseDTO buscarPorId(UUID id) {
        log.info("[SERVICE] Processando busca por id: " + id);
        return repository.buscarPorId(id)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new IllegalArgumentException("Usuario não encontrada: " + id));
    }

    public UsuarioResponseDTO atualizar(UUID id, UsuarioResquestDTO usuarioResquestDTO) {
        log.info("[SERVICE] Processando atualização por id: " + id);
        Usuario usuarioExistente = repository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario não encontrada: " + id));
        usuarioExistente.setNome(usuarioResquestDTO.nome());
        usuarioExistente.setEmail(usuarioResquestDTO.email());
        return toResponseDTO(repository.atualizar(usuarioExistente));
    }

    // Remove e devolve o usuário; se o id não existir, lança exceção
    public UsuarioResponseDTO deletar(UUID id) {
        log.info("[SERVICE] Processando deleção por id: " + id);
        return repository.deletar(id)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new IllegalArgumentException("Usuario não encontrada: " + id));
    }
}
