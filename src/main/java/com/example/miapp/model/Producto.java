package com.example.miapp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "productos")
@Schema(description = "Detalles de un cómic/manga en el catálogo de ComiCommerce.")
public class Producto {

    @Id
    @Schema(description = "Identificador único de MongoDB.", example = "60a8b9f1d43a6d40003b5f92")
    private String id;

    @Schema(description = "Título completo del producto.", example = "Batman: The Killing Joke")
    private String nombre;

    @Schema(description = "Precio unitario del producto.", example = "54930.00")
    private Double precio;

    @Schema(description = "Descripción detallada del cómic/manga.", example = "El peor enemigo de Batman, el Joker, se ha escapado de la prisión de Arkham...")
    private String descripcion;

    @Schema(description = "Categoría del producto.", example = "DC")
    private String categoria;

    @Schema(description = "Ruta o URL de la imagen de portada.", example = "/products/killingjoke.jpg")
    private String img;

}