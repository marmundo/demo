package com.example.demo.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

/**
 * Trata as exceções de todos os controllers em um único lugar.
 * O cliente recebe apenas uma mensagem curta em JSON (sem stacktrace),
 * e o log registra uma linha de aviso em vez da pilha de chamadas.
 */
@Slf4j
@RestControllerAdvice
public class TratadorDeExcecoes {

    // 404: recurso não existe
    @ExceptionHandler(TarefaNaoEncontradaException.class)
    public ResponseEntity<Map<String, String>> naoEncontrada(TarefaNaoEncontradaException e) {
        log.warn("[ERRO] {}", e.getMessage());
        return resposta(HttpStatus.NOT_FOUND, e.getMessage());
    }

    // 400: regra de negócio violada (ex.: título vazio)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> argumentoInvalido(IllegalArgumentException e) {
        log.warn("[ERRO] {}", e.getMessage());
        return resposta(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    // 400: JSON malformado ou data em formato inválido
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> corpoIlegivel(HttpMessageNotReadableException e) {
        log.warn("[ERRO] Corpo da requisição inválido: {}", e.getMessage());
        return resposta(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido.");
    }

    // 400: parâmetro de URL com tipo errado (ex.: /tarefas/abc)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> tipoInvalido(MethodArgumentTypeMismatchException e) {
        log.warn("[ERRO] Valor inválido para '{}': {}", e.getName(), e.getValue());
        return resposta(HttpStatus.BAD_REQUEST, "Valor inválido para '" + e.getName() + "'.");
    }

    // 500: qualquer erro inesperado; o detalhe fica só no log
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> inesperado(Exception e) {
        log.error("[ERRO] Falha inesperada", e);
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor.");
    }

    private ResponseEntity<Map<String, String>> resposta(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(Map.of("erro", mensagem));
    }
}
