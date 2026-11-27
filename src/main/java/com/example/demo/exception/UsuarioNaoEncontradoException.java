package com.example.demo.exception;

import java.util.UUID;

/**
 * Lançada quando um usuário com o id informado não existe.
 * O TratadorDeExcecoes converte esta exceção em resposta HTTP 404.
 */
public class UsuarioNaoEncontradoException extends RuntimeException {
    public UsuarioNaoEncontradoException(UUID id) {
        super("Usuário não encontrado: " + id);
    }
}
