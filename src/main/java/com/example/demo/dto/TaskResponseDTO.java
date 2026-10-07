package com.example.demo.dto;

public record TaskResponseDTO(
        Long id,
        String titulo,
        boolean concluida,
        com.example.demo.model.Prioridade prioridade
) {}