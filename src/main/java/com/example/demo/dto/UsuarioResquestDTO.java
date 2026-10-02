package com.example.demo.dto;

import java.time.LocalDate;

/**
 * DTO de ENTRADA para criar/atualizar um usuário (o id é gerado pelo servidor, não vem do cliente).
 */
public record UsuarioResquestDTO(
        String nome,
        String email,
        String cargo
) {}
