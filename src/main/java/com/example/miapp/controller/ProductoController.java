package com.example.miapp.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.example.miapp.model.Producto;
import com.example.miapp.repository.ProductoRepository;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(
    origins = {
        "https://comicommerce.vercel.app",
        "https://mondongonzalo.up.railway.app"
    }
)
public class ProductoController {

    private final ProductoRepository productoRepository;

    public ProductoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }
    
    // HATEOAS
    private EntityModel<Producto> toModel(Producto product) {
        return EntityModel.of(product,
            linkTo(methodOn(ProductoController.class).getProductById(product.getId())).withSelfRel(),
            linkTo(methodOn(ProductoController.class).getAllProducts()).withRel("productos")
        );
    }

    // Obtener todos los productos
    @GetMapping
    public CollectionModel<EntityModel<Producto>> getAllProducts() {
        List<EntityModel<Producto>> products = productoRepository.findAll()
                .stream().map(this::toModel).toList();

        return CollectionModel.of(products,
                linkTo(methodOn(ProductoController.class).getAllProducts()).withSelfRel());
    }

    // Obtener producto por ID
    @GetMapping("/{id}")
    public EntityModel<Producto> getProductById(@PathVariable String id) {
        Producto product = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return toModel(product);
    }

    // Crear un nuevo producto
    @PostMapping
    public EntityModel<Producto> createProduct(@RequestBody Producto product) {
        Producto saved = productoRepository.save(product);
        return toModel(saved);  // usando tu helper toModel()
    }

    // Actualizar producto existente
    @PutMapping("/{id}")
    public EntityModel<Producto> updateProduct(@PathVariable String id, @RequestBody Producto productDetails) {
        Producto product = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setNombre(productDetails.getNombre());
        product.setPrecio(productDetails.getPrecio());
        product.setDescripcion(productDetails.getDescripcion());
        product.setCategoria(productDetails.getCategoria());
        product.setImg(productDetails.getImg());

        productoRepository.save(product);

        return toModel(product);
    }

    // Eliminar producto
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable String id) {
        productoRepository.deleteById(id);
    }
}
