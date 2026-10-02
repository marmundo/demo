package com.example.demo.repository;

import com.example.demo.model.Usuario;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Implementação de UsuarioRepository que guarda os dados em memória.
 *
 * @Repository registra a classe como bean do Spring (camada de acesso a dados).
 * Quando a aplicação é encerrada, todos os dados são perdidos.
 */
@Repository
public class InMemoryUsuarioRepository implements UsuarioRepository{

    // "Banco de dados" simulado: chave = id, valor = usuário.
    // LinkedHashMap mantém a ordem de inserção ao listar.
    private final Map<Long, Usuario> banco = new LinkedHashMap<>();

    // Insere (ou substitui, se o id já existir) o usuário no mapa
    public Usuario salvar(Usuario usuario) {
        System.out.println("[REPOSITORY] Salvando Usuario em memória: " + usuario.getId());
        banco.put(usuario.getId(),usuario);
        return usuario;
    }

    // Devolve uma cópia da lista, para que quem chamou não altere o mapa interno
    public List<Usuario> listarTodas() {
        System.out.println("[REPOSITORY] Buscando todas as Usuarios em memória");
        return new ArrayList<>(banco.values());
    }

    // ofNullable: se o id não existir, map.get devolve null e o Optional fica vazio
    public Optional<Usuario> buscarPorId(Long id) {
        System.out.println("[REPOSITORY] Buscando Usuario por id: " + id);
        return Optional.ofNullable(banco.get(id));
    }

    // Map.remove devolve o valor removido (ou null se não existia)
    @Override
    public Optional<Usuario> deletar(Long id) {
        System.out.println("[REPOSITORY] Removendo Usuario por id: " + id);
        return Optional.ofNullable(banco.remove(id));
    }
}
