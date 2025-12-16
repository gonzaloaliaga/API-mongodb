package com.example.miapp.repository;

import com.example.miapp.model.Orden;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrdenRepository extends MongoRepository<Orden, String> {
    
}