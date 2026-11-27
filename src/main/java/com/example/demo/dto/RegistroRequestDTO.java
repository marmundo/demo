package com.example.demo.dto;

// Não há campo "perfil": o cadastro é público, então o cliente não pode escolher suas permissões
public record RegistroRequestDTO(String nome,
                                 String email,
                                 String senha) { }   // exige senha de pelo menos 8 caracteres
