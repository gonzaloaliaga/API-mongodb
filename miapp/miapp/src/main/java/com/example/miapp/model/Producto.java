package com.example.miapp.model;

import jakarta.persistence.*;
import lombok.*;

@Entity // Esta clase se convierte en tabla
@Data // Lombok genera getters/setters/toString/etc.
@NoArgsConstructor // Constructor vacío
@AllArgsConstructor // Constructor con todos los campos
public class Producto {

    @Id // Clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto incrementa el id
    private Long id;

    private String nombre;
    private Double precio;
    private String descripcion;
    private String categoria;

}

