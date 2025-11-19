package com.example.miapp.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.example.miapp.model.Producto;
import com.example.miapp.repository.ProductoRepository;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*") // Permite peticiones desde frontend
public class ProductoController {

    private final ProductoRepository productoRepository;

    public ProductoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // Obtener todos los productos
    @GetMapping
    public List<Producto> getAllProducts() {
        return productoRepository.findAll();
    }

    // Obtener producto por ID
    @GetMapping("/{id}")
    public Producto getProductById(@PathVariable String id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    // Crear un nuevo producto
    @PostMapping
    public Producto createProduct(@RequestBody Producto product) {
        return productoRepository.save(product);
    }

    // Actualizar producto existente
    @PutMapping("/{id}")
    public Producto updateProduct(@PathVariable String id, @RequestBody Producto productDetails) {
        Producto product = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setNombre(productDetails.getNombre());
        product.setPrecio(productDetails.getPrecio());
        product.setDescripcion(productDetails.getDescripcion());
        product.setCategoria(productDetails.getCategoria());
        
        return productoRepository.save(product);
    }

    // Eliminar producto
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable String id) {
        productoRepository.deleteById(id);
    }
}
