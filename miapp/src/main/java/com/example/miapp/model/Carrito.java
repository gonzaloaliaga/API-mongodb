package com.example.miapp.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import lombok.Data;
import java.util.List;

@Data
@Document(collection = "carritos")
public class Carrito {

    @Id
    private String id;

    private String usuarioId; // Referencia al usuario

    private List<CarritoItem> items;
}
