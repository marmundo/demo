package com.example.demo.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
// UserDetails é o "contrato" que o Spring Security entende para representar um usuário.
// Implementando-o, a própria entidade pode ser usada na autenticação, sem classe adaptadora.
public class Usuario implements UserDetails {

    @Id
    private UUID id = UUID.randomUUID();   // UUID evita IDs sequenciais previsíveis

    private String nome;
    private String email;                  // será o "login" (username) do usuário
    private String senha;                  // guarda o HASH BCrypt, nunca a senha em texto aberto

    // STRING grava "ADMIN" no banco; o padrão (ORDINAL) gravaria 1 e quebraria se o enum mudasse de ordem
    @Enumerated(EnumType.STRING)
    private Perfil perfil;

    protected Usuario() { }   // exigido pelo JPA; protected impede uso acidental no código da aplicação

    // O construtor recebe a senha JÁ criptografada: quem cria o usuário é responsável por aplicar o hash
    public Usuario(String nome, String email, String senhaCriptografada, Perfil perfil) {
        this.nome = nome;
        this.email = email;
        this.senha = senhaCriptografada;
        this.perfil = perfil;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Converte o Perfil do nosso domínio na "permissão" que o Spring entende.
        // O prefixo ROLE_ permite usar hasRole("ADMIN") na configuração
        return List.of(new SimpleGrantedAuthority("ROLE_" + perfil.name()));
    }

    // O Spring compara o que o cliente enviou com este valor (o hash) usando o PasswordEncoder
    @Override public String getPassword() { return senha; }
    // Aqui definimos que o "username" do Spring Security é o e-mail
    @Override public String getUsername() { return email; }

    // Não há getSenha(): evita expor o hash por descuido (ex.: ao serializar para JSON)
    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public Perfil getPerfil() { return perfil; }
}