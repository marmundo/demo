package com.example.demo.repository;

import com.example.demo.model.Usuario;

import java.util.*;

public class InMemoryUsuarioRepository implements UsuarioRepository{

    private final Map<Long, Usuario> banco = new LinkedHashMap<>();

    public Usuario salvar(Usuario usuario) {
        System.out.println("[REPOSITORY] Salvando Usuario em memória: " + usuario.getId());
        banco.put(usuario.getId(),usuario);
        return usuario;
    }
    public List<Usuario> listarTodas() {
        System.out.println("[REPOSITORY] Buscando todas as Usuarios em memória");
        return new ArrayList<>(banco.values());
    }

    public Optional<Usuario> buscarPorId(Long id) {
        System.out.println("[REPOSITORY] Buscando Usuario por id: " + id);
        return Optional.ofNullable(banco.get(id));
    }

    @Override
    public <T> ScopedValue<T> deletar(Long id) {
        return null;
    }
}
