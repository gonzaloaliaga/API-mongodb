package com.example.miapp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.miapp.model.Producto;

public interface ProductoRepository extends MongoRepository<Producto, String> {
}
