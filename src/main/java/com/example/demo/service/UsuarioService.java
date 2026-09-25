package com.example.demo.service;

import com.example.demo.dto.UsuarioResponseDTO;
import com.example.demo.dto.UsuarioResquestDTO;
import com.example.demo.model.Usuario;
import com.example.demo.repository.InMemoryUsuarioRepository;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UsuarioService {
    private final UsuarioRepository repository;
    private final AtomicLong sequencia = new AtomicLong();

    public UsuarioService(InMemoryUsuarioRepository repository) {
        this.repository = repository;
    }


    public UsuarioResponseDTO criar(UsuarioResponseDTO dto) {
        String titulo=dto.titulo();
        Usuario usuario = new Usuario(sequencia.incrementAndGet(),dto.titulo(),dto.descricao(),null);
        System.out.println("[SERVICE] Validando regra de negócio para: " +
                titulo);
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("O título da usuario não pode ser vazio.");
        }
        Usuario salva= repository.salvar(usuario);
        return toResponseDTO(salva);
    }

    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getCargo()
        );
    }

    public List<Usuario> listar() {
        System.out.println("[SERVICE] Solicitando lista de usuarios ao repository");
        return repository.listarTodas();
    }
    public Usuario buscarPorId(Long id) {
        System.out.println("[SERVICE] Processando busca por id: " + id);
        return repository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario não encontrada: " + id));
    }

    public UsuarioResponseDTO atualizar(Long id, UsuarioResquestDTO usuarioResquestDTO) {
        System.out.println("[SERVICE] Processando busca por id: " + id);
        return repository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario não encontrada: " + id));
    }

    public Usuario deletar(Long id) {
        return repository.deletar(id).orElseThrow(() -> new IllegalArgumentException("Usuario não encontrada: " + id));
    }
}