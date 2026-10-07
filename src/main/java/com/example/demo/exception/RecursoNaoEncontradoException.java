package com.example.demo.exception;

/**
 * Lançada quando um recurso referenciado (usuário ou prioridade) não existe.
 * O TratadorDeExcecoes converte esta exceção em resposta HTTP 404.
 */
public class RecursoNaoEncontradoException extends RuntimeException {
    public RecursoNaoEncontradoException(String recurso, Long id) {
        super(recurso + " não encontrado(a): " + id);
    }
}
