package com.example.miapp.controller;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
public class CarritoController {

    private final CarritoRepository carritoRepository;

    public CarritoController(CarritoRepository carritoRepository) {
        this.carritoRepository = carritoRepository;
    }

    // Obtener carrito del usuario
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
    @PostMapping("/{usuarioId}/add")
    public Carrito addItem(
            @PathVariable String usuarioId,
            @RequestBody CarritoItem itemRequest) {

        Carrito carrito = getCarrito(usuarioId);
        List<CarritoItem> items = carrito.getItems();

        Optional<CarritoItem> encontrado = items.stream()
                .filter(i -> i.getProductoId().equals(itemRequest.getProductoId()))
                .findFirst();

        if (encontrado.isPresent()) {
            encontrado.get().setCantidad(encontrado.get().getCantidad() + itemRequest.getCantidad());
        } else {
            items.add(itemRequest);
        }

        carrito.setItems(items);
        return carritoRepository.save(carrito);
    }

    // Disminuir cantidad o eliminar producto si llega a 0
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
                    return true; // se elimina si queda en 0
                }
            }
            return false;
        });

        carrito.setItems(items);
        return carritoRepository.save(carrito);
    }

    // Vaciar carrito
    @DeleteMapping("/{usuarioId}")
    public void vaciarCarrito(@PathVariable String usuarioId) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId).orElse(null);
        if (carrito != null) {
            carritoRepository.delete(carrito);
        }
    }
}
