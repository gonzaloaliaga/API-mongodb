package com.example.miapp.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import com.example.miapp.model.Usuario;
import com.example.miapp.repository.UsuarioRepository;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(
    origins = {
        "https://comicommerce.vercel.app",
        "https://mondongonzalo.up.railway.app"
    }
)
@Tag(name = "Usuarios", description = "Operaciones de gestion, registro e inicio de sesion de usuarios.")
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
    @Operation(summary = "Obtener todos los usuarios", description = "Devuelve la lista completa de todos los usuarios registrados en el sistema.")
    @GetMapping
    public CollectionModel<EntityModel<Usuario>> getAllUsers() {
        List<EntityModel<Usuario>> usuarios = usuarioRepository.findAll().stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(usuarios,
                linkTo(methodOn(UsuarioController.class).getAllUsers()).withSelfRel());
    }

    // Obtener usuario por ID
    @Operation(summary = "Obtener usuario por ID", description = "Busca un usuario específico usando su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public EntityModel<Usuario> getUserById(@PathVariable String id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return toModel(usuario);
    }

    // Login con correo y contraseña
    @Operation(summary = "Iniciar Sesión", description = "Verifica credenciales de correo y contraseña. Retorna el objeto Usuario si son correctas.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inicio de sesion exitoso"),
        @ApiResponse(responseCode = "401", description = "Credenciales invalidas")
    })
    @GetMapping("/login")
    public Usuario loginUser(
            @RequestParam String correo,
            @RequestParam String pass) {

        return usuarioRepository.findByCorreo(correo)
                .filter(u -> u.getPass().equals(pass))
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));
    }

    // Crear un nuevo usuario
    @Operation(summary = "Registrar nuevo usuario", description = "Crea una nueva cuenta de usuario en la base de datos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario registrado y devuelto"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos")
    })
    @PostMapping
    public EntityModel<Usuario> createUser(@RequestBody Usuario usuario) {
        
        if (usuario.getRol() == null || usuario.getRol().isBlank()) {
            usuario.setRol("cliente");
        }
        Usuario saved = usuarioRepository.save(usuario);
        return toModel(saved);
    }

    // Actualizar usuario existente
    @Operation(summary = "Actualizar usuario", description = "Modifica los datos de un usuario existente.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
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
        if (userDetails.getRol() != null) usuario.setRol(userDetails.getRol());

        usuarioRepository.save(usuario);

        return toModel(usuario);
    }

    // Eliminar usuario
    @Operation(summary = "Eliminar usuario", description = "Elimina permanentemente un usuario de la base de datos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario eliminado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        usuarioRepository.deleteById(id);
    }
}