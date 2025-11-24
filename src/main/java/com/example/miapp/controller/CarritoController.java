package com.example.miapp.controller;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.miapp.model.Carrito;
import com.example.miapp.model.CarritoItem;
import com.example.miapp.repository.CarritoRepository;

import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

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

    // HATEOAS
    private EntityModel<Carrito> toModel(Carrito carrito) {
        return EntityModel.of(carrito,
            linkTo(methodOn(CarritoController.class).getCarrito(carrito.getUsuarioId())).withSelfRel(),
            linkTo(methodOn(UsuarioController.class).getUserById(carrito.getUsuarioId()))
                .withRel("usuario")
        );
    }

    // Obtener carrito del usuario
    @GetMapping("/{usuarioId}")
    public EntityModel<Carrito> getCarrito(@PathVariable String usuarioId) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> carritoRepository.save(new Carrito(null, usuarioId, new ArrayList<>())));
        
        return toModel(carrito);
    }

    // Agregar producto o aumentar cantidad
    @PostMapping("/{usuarioId}/add")
    public EntityModel<Carrito> addItem(
            @PathVariable String usuarioId,
            @RequestBody CarritoItem itemRequest) {

        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> carritoRepository.save(new Carrito(null, usuarioId, new ArrayList<>())));

        List<CarritoItem> items = carrito.getItems();
        Optional<CarritoItem> encontrado = items.stream()
                .filter(i -> i.getProductoId().equals(itemRequest.getProductoId()))
                .findFirst();

        if (encontrado.isPresent()) {
            encontrado.get().setCantidad(encontrado.get().getCantidad() + itemRequest.getCantidad());
        } else {
            items.add(itemRequest);
        }

        carritoRepository.save(carrito);
        return toModel(carrito);
    }

    // Disminuir cantidad o eliminar producto si llega a 0
    @PutMapping("/{usuarioId}/remove/{productoId}")
    public EntityModel<Carrito> removeItem(
            @PathVariable String usuarioId,
            @PathVariable String productoId) {

        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> carritoRepository.save(new Carrito(null, usuarioId, new ArrayList<>())));

        List<CarritoItem> items = carrito.getItems();

        items.removeIf(i -> {
            if (i.getProductoId().equals(productoId)) {
                int nuevaCantidad = i.getCantidad() - 1;

                if (nuevaCantidad > 0) {
                    i.setCantidad(nuevaCantidad);
                    return false; // mantener item
                }

                return true; // eliminar item porque llega a 0
            }
            return false;
        });

        carrito.setItems(items);
        carritoRepository.save(carrito);

        return toModel(carrito);
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
