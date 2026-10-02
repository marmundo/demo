package com.example.demo.exception;

/**
 * Lançada quando uma tarefa com o id informado não existe.
 * O TratadorDeExcecoes converte esta exceção em resposta HTTP 404.
 */
public class TarefaNaoEncontradaException extends RuntimeException {
    public TarefaNaoEncontradaException(Long id) {
        super("Tarefa não encontrada: " + id);
    }
}
