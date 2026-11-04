package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de ENTRADA para criar/atualizar um usuário (o id é gerado pelo servidor, não vem do cliente).
 *
 * BEAN VALIDATION: as regras ficam no DTO (a "porta de entrada" da API), e não na regra de
 * negócio do Service. Assim dados inválidos são rejeitados cedo, com 400, sem chegar às
 * camadas internas. A validação é disparada pelo @Valid no UsuarioController.
 */
public record UsuarioResquestDTO(
        // Várias anotações no mesmo campo: todas precisam ser satisfeitas.
        @NotBlank(message = "O nome é obrigatório.")
        @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
        String nome,

        // @Email verifica o formato (algo@dominio). Atenção: ela aceita null/vazio,
        // por isso é combinada com @NotBlank para tornar o campo obrigatório.
        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "O e-mail informado é inválido.")
        String email,

        // Campo opcional, mas, se vier preenchido, tem tamanho máximo.
        @Size(max = 50, message = "O cargo deve ter no máximo 50 caracteres.")
        String cargo
) {}
