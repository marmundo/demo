package com.example.demo.dto;

/**
 * DTO de SAÍDA: representa o que a API devolve ao cliente sobre uma tarefa.
 * Expõe apenas os campos desejados; a prioridade vai como String (nome)
 * e o usuário responsável como id e nome (nulos quando a tarefa não tem dono).
 */
public record TaskResponseDTO(
        Long id,
        String titulo,
        boolean concluida,
        String prioridade,
        Long usuarioId,
        String usuarioNome
) {}
