package com.example.demo.repository;

import com.example.demo.model.Prioridade;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Acesso aos dados de Prioridade; a implementação é gerada pelo Spring Data JPA.
 */
public interface PrioridadeRepository extends JpaRepository<Prioridade, Long> {
    // Query derivada: WHERE nome = ?  (Optional porque pode não existir)
    Optional<Prioridade> findByNome(String nome);
}
