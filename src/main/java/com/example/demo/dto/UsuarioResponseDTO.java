package com.example.demo.dto;

import com.example.demo.model.Perfil;
import java.util.UUID;

/**
 * DTO de SAÍDA: dados do usuário devolvidos pela API após a criação.
 */
public record UsuarioResponseDTO(
        UUID id,
        String nome,
        String email,
        Perfil cargo
) {
}

