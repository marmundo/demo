package com.example.demo.exception;

/**
 * Lançada quando o e-mail informado já pertence a outro usuário.
 * O TratadorDeExcecoes converte esta exceção em resposta HTTP 409.
 */
public class EmailJaCadastradoException extends RuntimeException {
    public EmailJaCadastradoException() {
        super("E-mail já cadastrado.");
    }
}
