package com.example.demo.repository;

import com.example.demo.model.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acesso aos dados de Usuario; a implementação é gerada pelo Spring Data JPA.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
