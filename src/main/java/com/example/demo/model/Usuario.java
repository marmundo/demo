package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade JPA que representa um usuário (linha da tabela "usuarios").
 * Lado "um" da relação com Tarefa: um usuário tem várias tarefas.
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String cargo;

    // Lado inverso da relação: "usuario" é o atributo de Tarefa que guarda a chave estrangeira.
    // @JsonIgnore evita loop infinito (Usuario -> Tarefa -> Usuario) ao serializar em JSON.
    @OneToMany(mappedBy = "usuario")
    @JsonIgnore
    private List<Tarefa> tarefas = new ArrayList<>();

    // Construtor de conveniência (sem id, que é gerado pelo banco).
    // O construtor vazio, exigido pelo JPA, vem do @NoArgsConstructor.
    public Usuario(String nome, String email, String cargo) {
        this.nome = nome;
        this.email = email;
        this.cargo = cargo;
    }
}
