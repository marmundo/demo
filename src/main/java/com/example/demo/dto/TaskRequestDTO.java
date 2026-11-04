package com.example.demo.dto;

import java.time.LocalDate;

import com.example.demo.model.Prioridade;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO (Data Transfer Object) de ENTRADA: define quais campos o cliente pode enviar
 * ao criar uma tarefa. Fica separado da entidade Tarefa para que a API não dependa
 * da estrutura do banco (e o cliente não consiga, por exemplo, escolher o id).
 *
 * "record" (Java 16+) gera automaticamente construtor, getters (titulo(), descricao()...),
 * equals, hashCode e toString. É imutável, ideal para DTOs.
 *
 * BEAN VALIDATION: as anotações abaixo (jakarta.validation.constraints) DECLARAM as regras
 * de cada campo. Elas não executam nada sozinhas: a validação só acontece quando o
 * Controller recebe este objeto marcado com @Valid. Se alguma regra falhar, o Spring lança
 * MethodArgumentNotValidException ANTES de o método do controller ser chamado
 * (ver TratadorDeExcecoes, que converte o erro em HTTP 400).
 *
 * O atributo "message" é o texto devolvido ao cliente quando a regra é violada.
 */
public record TaskRequestDTO(
        // @NotBlank = não pode ser null, vazio ("") nem só espaços ("   ").
        // (@NotNull só barra null; @NotEmpty barra null e "", mas aceita "   ".)
        // @Size limita o tamanho; o mesmo limite (100) existe na coluna da entidade Tarefa.
        @NotBlank(message = "O título é obrigatório.")
        @Size(max = 100, message = "O título deve ter no máximo 100 caracteres.")
        String titulo,

        // Campo opcional: sem @NotBlank, null é aceito. @Size só avalia valores não nulos.
        @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres.")
        String descricao,

        // @FutureOrPresent: se informado, não pode ser uma data passada. Também aceita null.
        @FutureOrPresent(message = "O prazo não pode ser uma data passada.")
        LocalDate prazo,

        // Sem anotação: qualquer valor do enum (ou null) é aceito.
        // Valor fora do enum nem chega à validação: falha antes, na leitura do JSON.
        Prioridade prioridade,

        boolean concluida
) {}
