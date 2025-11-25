package com.example.miapp.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Productos", description = "Operaciones CRUD para el catalogo de ComiCommerce.")
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
    @Operation(summary = "Obtener Catalogo Completo", description = "Devuelve la lista completa de todos los productos disponibles en el catalogo.")
    @GetMapping
    public CollectionModel<EntityModel<Producto>> getAllProducts() {
        List<EntityModel<Producto>> products = productoRepository.findAll()
                .stream().map(this::toModel).toList();

        return CollectionModel.of(products,
                linkTo(methodOn(ProductoController.class).getAllProducts()).withSelfRel());
    }

    // Obtener producto por ID
    @Operation(summary = "Obtener Producto por ID", description = "Busca un producto específico usando su ID.")
    @GetMapping("/{id}")
    public EntityModel<Producto> getProductById(@PathVariable String id) {
        Producto product = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return toModel(product);
    }

    // Crear un nuevo producto
    @Operation(summary = "Crear Producto", description = "Agrega un nuevo producto al catalogo.")
    @PostMapping
    public EntityModel<Producto> createProduct(@RequestBody Producto product) {
        Producto saved = productoRepository.save(product);
        return toModel(saved);  // usando tu helper toModel()
    }

    // Actualizar producto existente
    @Operation(summary = "Actualizar Producto", description = "Modifica todos los datos de un producto existente usando su ID.")
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
    @Operation(summary = "Eliminar Producto", description = "Elimina un producto del catalogo de forma permanente.")
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable String id) {
        productoRepository.deleteById(id);
    }
}