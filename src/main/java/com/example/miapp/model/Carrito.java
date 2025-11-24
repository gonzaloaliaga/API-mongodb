package com.example.miapp.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import lombok.Data;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Document(collection = "carritos")
@Schema(description = "Representa el carrito de compras asociado a un usuario. Almacena la referencia del usuario y la lista de ítems.")
public class Carrito {

    @Id
    @Schema(description = "Identificador único de MongoDB para el carrito.", example = "60a8b9...")
    private String id;

    @Schema(description = "ID del usuario al que pertenece este carrito.", example = "60a8b9...")
    private String usuarioId;

    @Schema(description = "Lista de productos que están actualmente en el carrito.")
    private List<CarritoItem> items;
}