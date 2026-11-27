package com.example.demo.dto;

import java.time.LocalDate;

import com.example.demo.model.Prioridade;

/**
 * DTO de ENTRADA para CRIAR uma tarefa. Não tem o campo "concluida": toda tarefa nasce
 * não concluída, e o status só muda na atualização (ver TaskRequestDTO).
 */
public record TaskCreateRequestDTO(
        String titulo,
        String descricao,
        LocalDate prazo,
        Prioridade prioridade
) {}
