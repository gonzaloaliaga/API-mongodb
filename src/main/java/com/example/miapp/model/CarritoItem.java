package com.example.miapp.model;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Representa un único producto dentro del carrito de compras, incluyendo la cantidad.")
public class CarritoItem {

    @Schema(description = "ID del producto al que se refiere este item. Coincide con el ID de un Producto.", example = "60a8b9...")
    private String productoId;
    
    @Schema(description = "Cantidad de este producto que el usuario tiene en el carrito.", example = "2")
    private int cantidad;
}