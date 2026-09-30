package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name="tarefas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tarefa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String titulo;
    private boolean concluida;
    @Enumerated(EnumType.STRING)
    private Prioridade prioridade;

}
