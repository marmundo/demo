package com.example.demo.dto;

/**
 * DTO de SAÍDA: dados do usuário devolvidos pela API após a criação.
 */
public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        String cargo
) {
}

