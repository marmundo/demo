package com.example.demo.dto;

import java.time.LocalDate;
public record UsuarioResquestDTO(
        String nome,
        String email,
        String cargo
) {}