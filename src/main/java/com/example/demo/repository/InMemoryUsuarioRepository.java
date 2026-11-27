package com.example.demo.repository;

import com.example.demo.model.Usuario;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Implementação de UsuarioRepository que guarda os dados em memória.
 *
 * @Repository registra a classe como bean do Spring (camada de acesso a dados).
 * Quando a aplicação é encerrada, todos os dados são perdidos.
 */
@Slf4j 
@Repository
public class InMemoryUsuarioRepository implements UsuarioRepository{

    // "Banco de dados" simulado: chave = id, valor = usuário.
    // LinkedHashMap mantém a ordem de inserção ao listar.
    private final Map<UUID, Usuario> banco = new LinkedHashMap<>();

    // Insere (ou substitui, se o id já existir) o usuário no mapa
    public Usuario salvar(Usuario usuario) {
        log.info("[REPOSITORY] Salvando Usuario em memória: " + usuario.getId());
        banco.put(usuario.getId(),usuario);
        return usuario;
    }

    public Optional<Usuario> findByEmail(String email) {
        List<Usuario> usuarios =  listarTodas();
        for (Usuario usuario : usuarios) {
            if (usuario.getEmail() != null && usuario.getEmail().equalsIgnoreCase(email)) {
                return Optional.of(usuario);
            }
        }
        return Optional.empty();
    }



    @Override
    public Usuario atualizar(Usuario usuario) {
        log.info("[REPOSITORY] Atualizando Usuario em memória: " + usuario.getId());
        banco.put(usuario.getId(), usuario);
        return usuario;
    }

    // Devolve uma cópia da lista, para que quem chamou não altere o mapa interno
    public List<Usuario> listarTodas() {
        log.info("[REPOSITORY] Buscando todas as Usuarios em memória");
        return new ArrayList<>(banco.values());
    }

    // ofNullable: se o id não existir, map.get devolve null e o Optional fica vazio
    public Optional<Usuario> buscarPorId(UUID id) {
        log.info("[REPOSITORY] Buscando Usuario por id: " + id);
        return Optional.ofNullable(banco.get(id));
    }

    // Map.remove devolve o valor removido (ou null se não existia)
    @Override
    public Optional<Usuario> deletar(UUID id) {
        System.out.println("[REPOSITORY] Removendo Usuario por id: " + id);
        return Optional.ofNullable(banco.remove(id));
    }
}
