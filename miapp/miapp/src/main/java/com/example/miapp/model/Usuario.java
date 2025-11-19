package com.comicommerce.ComiCommerce.model;

import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String nombre;
    private String correo;
    private String pass;
    private String telefono;
    private String region;
    private String comuna;

}

