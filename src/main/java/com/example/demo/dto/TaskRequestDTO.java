package com.example.demo.dto;

import java.time.LocalDate;

import com.example.demo.model.Prioridade;

/**
 * DTO (Data Transfer Object) de ENTRADA: define quais campos o cliente pode enviar
 * ao criar uma tarefa. Fica separado da entidade Tarefa para que a API não dependa
 * da estrutura do banco (e o cliente não consiga, por exemplo, escolher o id).
 *
 * "record" (Java 16+) gera automaticamente construtor, getters (titulo(), descricao()...),
 * equals, hashCode e toString. É imutável, ideal para DTOs.
 */
public record TaskRequestDTO(
        String titulo,
        String descricao,
        LocalDate prazo,
        Prioridade prioridade,
        boolean concluida
) {}
