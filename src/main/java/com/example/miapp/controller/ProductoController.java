package com.example.miapp.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.example.miapp.model.Producto;
import com.example.miapp.repository.ProductoRepository;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(
    origins = {
        "https://comicommerce.vercel.app",
        "https://mondongonzalo.up.railway.app"
    }
)
@Tag(name = "Productos", description = "Operaciones CRUD para el catalogo de ComiCommerce.")
public class ProductoController {

    private final ProductoRepository productoRepository;

    public ProductoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // Obtener todos los productos
    @Operation(summary = "Obtener Catalogo Completo", description = "Devuelve la lista completa de todos los productos disponibles en el catalogo.")
    @GetMapping
    public List<Producto> getAllProducts() {
        return productoRepository.findAll();
    }

    // Obtener producto por ID
    @Operation(summary = "Obtener Producto por ID", description = "Busca un producto específico usando su ID.")
    @GetMapping("/{id}")
    public Producto getProductById(@PathVariable String id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    // Crear un nuevo producto
    @Operation(summary = "Crear Producto", description = "Agrega un nuevo producto al catalogo.")
    @PostMapping
    public Producto createProduct(@RequestBody Producto product) {
        return productoRepository.save(product);
    }

    // Actualizar producto existente
    @Operation(summary = "Actualizar Producto", description = "Modifica todos los datos de un producto existente usando su ID.")
    @PutMapping("/{id}")
    public Producto updateProduct(@PathVariable String id, @RequestBody Producto productDetails) {
        Producto product = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setNombre(productDetails.getNombre());
        product.setPrecio(productDetails.getPrecio());
        product.setDescripcion(productDetails.getDescripcion());
        product.setCategoria(productDetails.getCategoria());
        product.setImg(productDetails.getImg());
        
        return productoRepository.save(product);
    }

    // Eliminar producto
    @Operation(summary = "Eliminar Producto", description = "Elimina un producto del catalogo de forma permanente.")
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable String id) {
        productoRepository.deleteById(id);
    }
}