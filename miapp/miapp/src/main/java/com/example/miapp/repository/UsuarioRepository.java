package com.example.miapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.miapp.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}