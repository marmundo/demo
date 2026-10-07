package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidade JPA que representa o nível de prioridade de uma tarefa (BAIXA, MEDIA, ALTA).
 * Está no lado "um" da relação: uma Prioridade pode ser usada por várias Tarefas
 * (a chave estrangeira prioridade_id fica na tabela "tarefas").
 * Os valores iniciais são carregados pelo arquivo data.sql.
 */
@Entity
@Table(name = "prioridades")
@Getter
@Setter
@NoArgsConstructor
public class Prioridade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nome único: evita duas prioridades "ALTA" na tabela
    @Column(nullable = false, unique = true, length = 30)
    private String nome;
}
