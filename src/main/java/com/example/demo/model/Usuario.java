package com.example.demo.model;

/**
 * Modelo de domínio de Usuario escrito "à mão" (POJO), sem Lombok e sem JPA.
 * Serve de comparação com a entidade Tarefa: aqui todo o código de construtor,
 * getters e setters é escrito manualmente; na Tarefa o Lombok o gera automaticamente.
 * Os usuários ficam apenas em memória (ver InMemoryUsuarioRepository).
 */
public class Usuario {
    private Long id;
    private String nome;
    private String email;
    private String cargo;

    public Usuario(Long id, String nome, String email, String cargo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.cargo = cargo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }
}
