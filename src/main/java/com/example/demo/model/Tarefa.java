package com.example.demo.model;

import java.time.LocalDate;

public class Tarefa {
    private Long id;
    private String titulo;
    private String descricao;
    private LocalDate prazo;
    private boolean concluida;
    private String prioridade;

    public Tarefa(Long id, String titulo, String descricao, LocalDate prazo) {
        this.id = id;
        this.titulo = titulo;
        this.descricao=descricao;
        this.prazo=prazo;
        this.concluida = false;
        this.prioridade="alta";
        System.out.println("Criando Tarefa...");
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public boolean isConcluida() {
        return concluida;
    }

    public String getPrioridade(){
        return this.prioridade;
    }

    public void setConcluida(boolean concluida) {
        this.concluida = concluida;
    }
}
