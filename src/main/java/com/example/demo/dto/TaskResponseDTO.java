package com.example.demo.dto;

/**
 * DTO de SAÍDA: representa o que a API devolve ao cliente sobre uma tarefa.
 * Expõe apenas os campos desejados; a prioridade vai como String (nome do enum).
 */
public record TaskResponseDTO(
        Long id,
        String titulo,
        boolean concluida,
        String prioridade
) {}
