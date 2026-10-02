package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidade JPA: cada objeto Tarefa corresponde a uma linha da tabela "tarefas".
 * O Hibernate cria a tabela a partir desta classe (ver spring.jpa.hibernate.ddl-auto).
 *
 * Lombok gera o código repetitivo em tempo de compilação:
 *  - @Getter / @Setter: métodos getX() e setX() de todos os atributos;
 *  - @NoArgsConstructor: construtor sem argumentos (exigido pelo JPA).
 */
@Entity
@Table(name="tarefas")
@Getter
@Setter
@NoArgsConstructor

public class Tarefa {
    // Chave primária. IDENTITY: o próprio banco gera o valor (auto incremento)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Coluna obrigatória (NOT NULL) com no máximo 100 caracteres
    @Column(nullable = false, length = 100)
    private String titulo;

    // Toda tarefa nasce como não concluída
    private boolean concluida=false;

    // EnumType.STRING grava o nome ("ALTA") no banco, e não a posição (2),
    // o que evita quebrar os dados se a ordem do enum mudar
    @Enumerated(EnumType.STRING)
    private Prioridade prioridade;

}
