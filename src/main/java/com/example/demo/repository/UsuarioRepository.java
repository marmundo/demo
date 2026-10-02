package com.example.demo.repository;

import com.example.demo.model.Usuario;

import java.util.List;
import java.util.Optional;

/**
 * Contrato (interface) de acesso aos dados de Usuario.
 * O Service depende desta abstração, não de como os dados são guardados:
 * é possível trocar a implementação em memória por uma com banco de dados
 * sem alterar a regra de negócio.
 *
 * Optional indica que o resultado pode não existir, evitando retornar null.
 */
public interface UsuarioRepository {
    public Usuario salvar(Usuario usuario);
    public List<Usuario> listarTodas();
    public Optional<Usuario> buscarPorId(Long id);

    public Optional<Usuario> deletar(Long id);
}
