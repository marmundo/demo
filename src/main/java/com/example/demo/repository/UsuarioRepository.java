package com.example.demo.repository;

import com.example.demo.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {
    public Usuario salvar(Usuario usuario);
    public List<Usuario> listarTodas();
    public Optional<Usuario> buscarPorId(Long id);

    <T> ScopedValue<T> deletar(Long id);
}
