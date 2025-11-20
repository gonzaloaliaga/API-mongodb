package com.example.miapp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;
import com.example.miapp.model.Carrito;

public interface CarritoRepository extends MongoRepository<Carrito, String> {
    Optional<Carrito> findByUsuarioId(String usuarioId);
}