package com.example.demo.dto;

import com.example.demo.model.Perfil;

public record RegistroRequestDTO(String nome,
                                 String email,
                                 String senha,   // exige senha de pelo menos 8 caracteres
                                 Perfil perfil) { }