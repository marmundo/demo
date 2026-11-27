package com.example.demo.service;

import com.example.demo.dto.UsuarioResponseDTO;
import com.example.demo.dto.UsuarioResquestDTO;
import com.example.demo.exception.EmailJaCadastradoException;
import com.example.demo.exception.UsuarioNaoEncontradoException;
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
        // Sem estes dados o usuário não conseguiria fazer login
        exigir("email", dto.email());
        exigir("senha", dto.senha());
        exigir("perfil", dto.perfil());
        recusarEmBranco("email", dto.email());
        recusarEmBranco("senha", dto.senha());
        if (repository.findByEmail(dto.email()).isPresent()) {
            throw new EmailJaCadastradoException();
        }

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
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));
    }

    // Atualiza somente os campos enviados: campo omitido (null) mantém o valor atual
    public UsuarioResponseDTO atualizar(UUID id, UsuarioResquestDTO dto) {
        log.info("[SERVICE] Processando atualização por id: " + id);
        Usuario usuarioExistente = repository.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));

        // Valida tudo antes de alterar: o repositório em memória guarda este mesmo objeto
        recusarEmBranco("nome", dto.nome());
        recusarEmBranco("email", dto.email());
        recusarEmBranco("senha", dto.senha());
        if (dto.email() != null && emailPertenceAOutro(dto.email(), id)) {
            throw new EmailJaCadastradoException();
        }

        if (dto.nome() != null) {
            usuarioExistente.setNome(dto.nome());
        }
        if (dto.email() != null) {
            usuarioExistente.setEmail(dto.email());
        }
        if (dto.senha() != null) {
            usuarioExistente.setSenha(passwordEncoder.encode(dto.senha()));
        }
        if (dto.perfil() != null) {
            usuarioExistente.setPerfil(dto.perfil());
        }
        return toResponseDTO(repository.atualizar(usuarioExistente));
    }

    private void exigir(String campo, Object valor) {
        if (valor == null) {
            throw new IllegalArgumentException("O campo '" + campo + "' é obrigatório.");
        }
    }

    private void recusarEmBranco(String campo, String valor) {
        if (valor != null && valor.isBlank()) {
            throw new IllegalArgumentException("O campo '" + campo + "' não pode ser vazio.");
        }
    }

    // O próprio usuário pode reenviar o e-mail que já tem
    private boolean emailPertenceAOutro(String email, UUID id) {
        return repository.findByEmail(email)
                .filter(outro -> !outro.getId().equals(id))
                .isPresent();
    }

    // Remove e devolve o usuário; se o id não existir, lança exceção
    public UsuarioResponseDTO deletar(UUID id) {
        log.info("[SERVICE] Processando deleção por id: " + id);
        return repository.deletar(id)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));
    }
}
