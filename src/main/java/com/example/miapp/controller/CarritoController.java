package com.example.miapp.controller;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import com.example.miapp.model.Carrito;
import com.example.miapp.model.CarritoItem;
import com.example.miapp.repository.CarritoRepository;

@RestController
@RequestMapping("/api/carrito")
@CrossOrigin(
    origins = {
        "https://comicommerce.vercel.app",
        "https://mondongonzalo.up.railway.app"
    }
)
@Tag(name = "Carrito", description = "Gestion del carrito de compras de los usuarios.")
public class CarritoController {

    private final CarritoRepository carritoRepository;

    public CarritoController(CarritoRepository carritoRepository) {
        this.carritoRepository = carritoRepository;
    }

    // Obtener carrito del usuario
    @Operation(summary = "Obtener Carrito", description = "Devuelve el carrito de compras actual de un usuario. Si no existe, crea uno vacio.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Carrito encontrado/Creado exitosamente.")
    })
    @GetMapping("/{usuarioId}")
    public Carrito getCarrito(@PathVariable String usuarioId) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId).orElse(null);
        if (carrito == null) {
            carrito = new Carrito();
            carrito.setUsuarioId(usuarioId);
            carrito.setItems(new ArrayList<>());
            carrito = carritoRepository.save(carrito);
        }
        return carrito;
    }

    // Agregar producto o aumentar cantidad
    @Operation(summary = "Agregar/Aumentar Producto", description = "Añade un producto al carrito o aumenta la cantidad si ya existe. Recibe un CarritoItem en el body.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto agregado/Cantidad actualizada.")
    })
    @PostMapping("/{usuarioId}/add")
    public Carrito addItem(
            @PathVariable String usuarioId,
            @RequestBody CarritoItem itemRequest) {
                
        Carrito carrito = getCarrito(usuarioId);
        if (carrito.getItems() == null) {
            carrito.setItems(new ArrayList<>());
        }

        List<CarritoItem> items = carrito.getItems();

        Optional<CarritoItem> encontrado = items.stream()
                .filter(i -> i.getProductoId().equals(itemRequest.getProductoId()))
                .findFirst();

        if (encontrado.isPresent()) {
            encontrado.get().setCantidad(encontrado.get().getCantidad() + itemRequest.getCantidad());
        } else {
            CarritoItem item = new CarritoItem();
            item.setProductoId(itemRequest.getProductoId());
            item.setCantidad(itemRequest.getCantidad());
            items.add(itemRequest);
        }
        return carritoRepository.save(carrito);
    }

    // Disminuir cantidad o eliminar producto si llega a 0
    @Operation(summary = "Disminuir o Eliminar Producto", description = "Disminuye la cantidad de un producto. Si la cantidad llega a cero, el producto se elimina del carrito.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cantidad actualizada/Producto eliminado.")
    })
    @PutMapping("/{usuarioId}/remove/{productoId}")
    public Carrito removeItem(
            @PathVariable String usuarioId,
            @PathVariable String productoId) {

        Carrito carrito = getCarrito(usuarioId);
        List<CarritoItem> items = carrito.getItems();

        items.removeIf(i -> {
            if (i.getProductoId().equals(productoId)) {
                int nuevaCantidad = i.getCantidad() - 1;
                if (nuevaCantidad > 0) {
                    i.setCantidad(nuevaCantidad);
                    return false;
                } else {
                    return true;
                }
            }
            return false;
        });

        carrito.setItems(items);
        return carritoRepository.save(carrito);
    }

    // Vaciar carrito
    @Operation(summary = "Vaciar Carrito", description = "Elimina permanentemente el documento de carrito asociado al usuario.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Carrito vaciado y eliminado.")
    })
    @DeleteMapping("/{usuarioId}")
    public void vaciarCarrito(@PathVariable String usuarioId) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId).orElse(null);
        if (carrito != null) {
            carritoRepository.delete(carrito);
        }
    }
}
