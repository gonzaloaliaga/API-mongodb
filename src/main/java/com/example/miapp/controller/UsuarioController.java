package com.example.miapp.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.example.miapp.model.Usuario;
import com.example.miapp.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(
    origins = {
        "https://comicommerce.vercel.app",
        "https://mondongonzalo.up.railway.app"
    }
)
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // HATEOAS
    private EntityModel<Usuario> toModel(Usuario usuario) {
        return EntityModel.of(usuario,
            linkTo(methodOn(UsuarioController.class).getUserById(usuario.getId())).withSelfRel(),
            linkTo(methodOn(UsuarioController.class).getAllUsers()).withRel("usuarios"),
            linkTo(methodOn(CarritoController.class).getCarrito(usuario.getId())).withRel("carrito")
        );
    }

    // Obtener todos los usuarios
   @GetMapping
    public CollectionModel<EntityModel<Usuario>> getAllUsers() {
        List<EntityModel<Usuario>> usuarios = usuarioRepository.findAll().stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(usuarios,
                linkTo(methodOn(UsuarioController.class).getAllUsers()).withSelfRel());
    }

    // Obtener usuario por ID
    @GetMapping("/{id}")
    public EntityModel<Usuario> getUserById(@PathVariable String id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return toModel(usuario);
    }

    // Login con correo y contraseña
    @GetMapping("/login")
    public Usuario loginUser(
            @RequestParam String correo,
            @RequestParam String password) {

        return usuarioRepository.findByCorreo(correo)
                .filter(u -> u.getPass().equals(password))
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));
    }

    // Crear un nuevo usuario
    @PostMapping
    public EntityModel<Usuario> createUser(@RequestBody Usuario usuario) {
        Usuario saved = usuarioRepository.save(usuario);
        return toModel(saved);
    }

    // Actualizar usuario existente
    @PutMapping("/{id}")
    public EntityModel<Usuario> updateUser(@PathVariable String id, @RequestBody Usuario userDetails) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userDetails.getNombre() != null) usuario.setNombre(userDetails.getNombre());
        if (userDetails.getCorreo() != null) usuario.setCorreo(userDetails.getCorreo());
        if (userDetails.getPass() != null) usuario.setPass(userDetails.getPass());
        if (userDetails.getTelefono() != null) usuario.setTelefono(userDetails.getTelefono());
        if (userDetails.getRegion() != null) usuario.setRegion(userDetails.getRegion());
        if (userDetails.getComuna() != null) usuario.setComuna(userDetails.getComuna());

        usuarioRepository.save(usuario);

        return toModel(usuario);
    }

    // Eliminar usuario
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        usuarioRepository.deleteById(id);
    }
}

